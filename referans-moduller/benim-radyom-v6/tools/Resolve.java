import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * Kucuk bir Maven bagimlilik cozumleyici.
 *
 * Gradle bu makinede calisamadigi icin (Cortex XDR loopback engeli) bagimlilik
 * agacini kendimiz yuruyoruz: POM indir, <dependencies> oku, kapanisa kadar
 * tekrarla, sonra .aar/.jar dosyalarini indir.
 */
public class Resolve {

    static final String[] REPOS = {
        "https://dl.google.com/dl/android/maven2/",
        "https://repo1.maven.org/maven2/"
    };

    static Path outDir;
    static Path pomCache;
    /** groupId:artifactId -> secilen surum */
    static final Map<String, String> chosen = new LinkedHashMap<>();
    /** islenmis g:a:v */
    static final Set<String> visited = new HashSet<>();
    static final Deque<String> queue = new ArrayDeque<>();
    static final Map<String, String> packaging = new HashMap<>();
    static final List<String> failures = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        outDir = Paths.get(args[0]);
        pomCache = outDir.resolve(".poms");
        Files.createDirectories(outDir);
        Files.createDirectories(pomCache);

        List<String> roots = new ArrayList<>();
        for (int i = 1; i < args.length; i++) roots.add(args[i]);
        for (String r : roots) enqueue(r);

        while (!queue.isEmpty()) {
            String gav = queue.poll();
            if (!visited.add(gav)) continue;
            try {
                processPom(gav);
            } catch (Exception e) {
                failures.add("POM " + gav + " -> " + e);
            }
        }

        System.out.println("--- cozulen " + chosen.size() + " artifact, indiriliyor ---");
        int ok = 0;
        for (Map.Entry<String, String> e : chosen.entrySet()) {
            String ga = e.getKey(), v = e.getValue();
            String pkg = packaging.getOrDefault(ga + ":" + v, "jar");
            if (pkg.equals("pom")) continue;
            try {
                if (download(ga, v, pkg)) ok++;
            } catch (Exception ex) {
                failures.add("ARTIFACT " + ga + ":" + v + " -> " + ex);
            }
        }
        System.out.println("indirilen: " + ok);
        if (!failures.isEmpty()) {
            System.out.println("--- sorunlar (" + failures.size() + ") ---");
            for (String f : failures) System.out.println("  " + f);
        }
    }

    static void enqueue(String gav) {
        String[] p = gav.split(":");
        if (p.length < 3) return;
        String ga = p[0] + ":" + p[1];
        String v = p[2];
        String cur = chosen.get(ga);
        if (cur == null || compareVersions(v, cur) > 0) {
            chosen.put(ga, v);
            queue.add(ga + ":" + v);
        }
    }

    static void processPom(String gav) throws Exception {
        String[] p = gav.split(":");
        String group = p[0], artifact = p[1], version = p[2];
        byte[] pomBytes = fetchPom(group, artifact, version);
        if (pomBytes == null) { failures.add("POM bulunamadi: " + gav); return; }

        Document doc = parse(pomBytes);
        Element root = doc.getDocumentElement();

        Map<String, String> props = new HashMap<>();
        props.put("project.version", version);
        props.put("project.groupId", group);
        Element propsEl = child(root, "properties");
        if (propsEl != null) {
            NodeList nl = propsEl.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    props.put(n.getNodeName(), n.getTextContent().trim());
                }
            }
        }

        String pkg = text(child(root, "packaging"), "jar");
        packaging.put(group + ":" + artifact + ":" + version, pkg);

        Element deps = child(root, "dependencies");
        if (deps == null) return;
        for (Element d : children(deps, "dependency")) {
            String scope = text(child(d, "scope"), "compile");
            String optional = text(child(d, "optional"), "false");
            if (!scope.equals("compile") && !scope.equals("runtime")) continue;
            if (optional.equals("true")) continue;

            String dg = subst(text(child(d, "groupId"), ""), props);
            String da = subst(text(child(d, "artifactId"), ""), props);
            String dv = subst(text(child(d, "version"), ""), props);
            if (dg.isEmpty() || da.isEmpty() || dv.isEmpty()) continue;
            if (dv.startsWith("[") || dv.startsWith("(")) {
                dv = dv.replaceAll("[\\[\\]()]", "").split(",")[0].trim();
                if (dv.isEmpty()) continue;
            }
            enqueue(dg + ":" + da + ":" + dv);
        }
    }

    static String subst(String s, Map<String, String> props) {
        if (s == null) return "";
        int guard = 0;
        while (s.contains("${") && guard++ < 5) {
            int a = s.indexOf("${"), b = s.indexOf('}', a);
            if (b < 0) break;
            String key = s.substring(a + 2, b);
            String val = props.get(key);
            if (val == null) return "";
            s = s.substring(0, a) + val + s.substring(b + 1);
        }
        return s.trim();
    }

    static byte[] fetchPom(String g, String a, String v) throws Exception {
        String rel = g.replace('.', '/') + "/" + a + "/" + v + "/" + a + "-" + v + ".pom";
        Path cached = pomCache.resolve((g + "-" + a + "-" + v + ".pom").replace(':', '_'));
        if (Files.exists(cached)) return Files.readAllBytes(cached);
        for (String repo : REPOS) {
            byte[] data = get(repo + rel);
            if (data != null) { Files.write(cached, data); return data; }
        }
        return null;
    }

    static boolean download(String ga, String v, String pkg) throws Exception {
        String[] p = ga.split(":");
        String g = p[0], a = p[1];
        String ext = pkg.equals("aar") ? "aar" : "jar";
        Path target = outDir.resolve(a + "-" + v + "." + ext);
        if (Files.exists(target) && Files.size(target) > 0) return true;

        String rel = g.replace('.', '/') + "/" + a + "/" + v + "/" + a + "-" + v + ".";
        for (String repo : REPOS) {
            byte[] data = get(repo + rel + ext);
            if (data == null && ext.equals("jar")) data = get(repo + rel + "aar");
            if (data == null && ext.equals("aar")) data = get(repo + rel + "jar");
            if (data != null) {
                Files.write(target, data);
                System.out.println("  + " + target.getFileName() + " (" + (data.length / 1024) + " KB)");
                return true;
            }
        }
        failures.add("indirilemedi: " + ga + ":" + v);
        return false;
    }

    static byte[] get(String url) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setConnectTimeout(30000);
            c.setReadTimeout(120000);
            c.setInstanceFollowRedirects(true);
            if (c.getResponseCode() != 200) return null;
            try (InputStream in = c.getInputStream();
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[65536];
                int n;
                while ((n = in.read(buf)) > 0) bos.write(buf, 0, n);
                return bos.toByteArray();
            }
        } catch (Exception e) {
            return null;
        }
    }

    static Document parse(byte[] data) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(false);
        return f.newDocumentBuilder().parse(new ByteArrayInputStream(data));
    }

    static Element child(Element parent, String name) {
        if (parent == null) return null;
        NodeList nl = parent.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(name)) return (Element) n;
        }
        return null;
    }

    static List<Element> children(Element parent, String name) {
        List<Element> out = new ArrayList<>();
        if (parent == null) return out;
        NodeList nl = parent.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(name)) out.add((Element) n);
        }
        return out;
    }

    static String text(Element e, String def) {
        return e == null ? def : e.getTextContent().trim();
    }

    /** Kaba ama AndroidX icin yeterli surum karsilastirmasi. */
    static int compareVersions(String a, String b) {
        String[] x = a.split("[.\\-]"), y = b.split("[.\\-]");
        for (int i = 0; i < Math.max(x.length, y.length); i++) {
            String xs = i < x.length ? x[i] : "0";
            String ys = i < y.length ? y[i] : "0";
            int xi = num(xs), yi = num(ys);
            if (xi >= 0 && yi >= 0) {
                if (xi != yi) return Integer.compare(xi, yi);
            } else {
                int c = xs.compareTo(ys);
                if (c != 0) return c;
            }
        }
        return 0;
    }

    static int num(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return -1; }
    }
}
