package server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import model.Proposal;
import facade.SubmitFacade;
import observer.EmailNotificationObserver;
import observer.SystemLogObserver;
import proxy.DocumentStorageProxy;
import proxy.RealDocumentStorage;
import strategy.ValidationStrategyFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SimulasiServer {

    private static final int PORT = 8080;

    public static void start() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new StaticHandler());
        server.createContext("/simulate", new SimulateHandler());
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("Server berjalan di http://localhost:" + PORT);
        System.out.println("Tekan Ctrl+C untuk menghentikan server.");
        // Block main thread so JVM doesn't exit (HttpServer runs on daemon threads)
        Thread.currentThread().join();
    }

    // ── Serve index.html ──────────────────────────────────────────────────

    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String htmlPath = "web/index.html";
            byte[] body;
            try {
                body = Files.readAllBytes(Paths.get(htmlPath));
            } catch (IOException e) {
                String err = "File tidak ditemukan: " + htmlPath;
                body = err.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(404, body.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(body); }
                return;
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(body); }
        }
    }

    // ── Handle POST /simulate ─────────────────────────────────────────────

    static class SimulateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String requestBody;
            try (InputStream is = exchange.getRequestBody()) {
                requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }

            String jsonResponse;
            try {
                jsonResponse = runSimulation(requestBody);
            } catch (Exception e) {
                jsonResponse = "{\"success\":false,\"statusAkhir\":\"ERROR\",\"logs\":[{\"tag\":\"SYSTEM\",\"pesan\":\""
                        + escapeJson(e.getMessage()) + "\"}]}";
            }

            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(responseBytes); }
        }
    }

    // ── Simulation logic ──────────────────────────────────────────────────

    static String runSimulation(String json) {
        // Parse input
        String namaMahasiswa  = parseString(json, "namaMahasiswa",  "Mahasiswa");
        String nim            = parseString(json, "nim",            "00/000000/TK/000");
        String jenisPkm       = parseString(json, "jenisPkm",       "PKM-K");
        String namaFile       = parseString(json, "namaFile",       "proposal.pdf");
        double ukuranMb       = parseDouble(json, "ukuranMb",       1.0);
        boolean isAuth        = parseBool(json, "isAuthenticated",  true);
        boolean anggaranKas   = parseBool(json, "hasAnggaranKas",   false);
        boolean susunanTim    = parseBool(json, "susunanTimLengkap",false);
        boolean izinLab       = parseBool(json, "hasIzinLab",       false);
        boolean pembimbing    = parseBool(json, "hasPembimbing",    false);
        boolean referensi     = parseBool(json, "hasReferensiJurnal",false);
        boolean suratMitra    = parseBool(json, "hasSuratIzinMitra",false);
        boolean lokasiKegiatan= parseBool(json, "hasLokasiKegiatan",false);
        boolean forceVerifying = parseBool(json, "forceVerifying",  false);

        // Capture all System.out output
        List<String> rawLogs = new ArrayList<>();
        PrintStream originalOut = System.out;

        CaptureStream capture = new CaptureStream(originalOut, rawLogs);
        System.setOut(capture);

        String statusAkhir;
        try {
            String proposalId = "PKM-2024-SIM";
            Proposal proposal = new Proposal(
                    proposalId, namaMahasiswa, nim, jenisPkm, namaFile, ukuranMb)
                    .withAuthenticated(isAuth)
                    .withAnggaranKas(anggaranKas)
                    .withSusunanTim(susunanTim)
                    .withIzinLab(izinLab)
                    .withPembimbing(pembimbing)
                    .withReferensiJurnal(referensi)
                    .withSuratIzinMitra(suratMitra)
                    .withLokasiKegiatan(lokasiKegiatan);

            proposal.addObserver(new EmailNotificationObserver());
            proposal.addObserver(new SystemLogObserver());

            if (forceVerifying) {
                // Skenario 5: simulasikan proposal sudah VERIFYING
                System.out.println("[SYSTEM]   Menyiapkan skenario: memindahkan proposal ke status VERIFYING...");
                proposal.upload();
                proposal.verify();
                System.out.println("[SYSTEM]   Status proposal saat ini: " + proposal.getStateName());
                System.out.println("[SYSTEM]   ─────────────────────────────────────────────────────────────");
                System.out.println("[SYSTEM]   Simulasi: mahasiswa panik, mencoba upload ulang...");
                System.out.printf("[FACADE]   Mencoba upload ulang untuk: %s (status: %s)%n",
                        proposal.getId(), proposal.getStateName());
                proposal.upload(); // akan diblokir VerifyingState
                System.out.println("[FACADE]   Percobaan upload ulang ditolak ⛔");
                statusAkhir = "DIBLOKIR";
            } else {
                SubmitFacade facade = new SubmitFacade(
                        new DocumentStorageProxy(new RealDocumentStorage()),
                        new ValidationStrategyFactory()
                );
                facade.processUpload(proposal);
                statusAkhir = proposal.getStateName();
            }
        } finally {
            System.setOut(originalOut);
        }

        // Parse raw logs into structured entries
        List<String[]> entries = new ArrayList<>();
        for (String line : rawLogs) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String tag = "SYSTEM";
            String pesan = line;
            if (line.startsWith("[") && line.contains("]")) {
                int end = line.indexOf("]");
                tag = line.substring(1, end).trim();
                pesan = line.substring(end + 1).trim();
            }
            entries.add(new String[]{tag, pesan});
        }

        // Build JSON response
        StringBuilder sb = new StringBuilder();
        sb.append("{\"success\":true,\"statusAkhir\":\"").append(escapeJson(statusAkhir)).append("\",\"logs\":[");
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("{\"tag\":\"").append(escapeJson(entries.get(i)[0])).append("\",")
              .append("\"pesan\":\"").append(escapeJson(entries.get(i)[1])).append("\"}");
        }
        sb.append("]}");
        return sb.toString();
    }

    // ── CaptureStream: intercept System.out writes ────────────────────────

    static class CaptureStream extends PrintStream {
        private final List<String> buffer;
        private final StringBuilder lineBuilder = new StringBuilder();

        CaptureStream(OutputStream mirror, List<String> buffer) {
            super(mirror, true, StandardCharsets.UTF_8);
            this.buffer = buffer;
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            super.write(buf, off, len);
            String chunk = new String(buf, off, len, StandardCharsets.UTF_8);
            for (char c : chunk.toCharArray()) {
                if (c == '\n') {
                    buffer.add(lineBuilder.toString());
                    lineBuilder.setLength(0);
                } else if (c != '\r') {
                    lineBuilder.append(c);
                }
            }
        }

        @Override
        public void flush() {
            super.flush();
            if (lineBuilder.length() > 0) {
                buffer.add(lineBuilder.toString());
                lineBuilder.setLength(0);
            }
        }
    }

    // ── JSON helpers ──────────────────────────────────────────────────────

    static String parseString(String json, String key, String defaultVal) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx < 0) return defaultVal;
        int colon = json.indexOf(":", idx + search.length());
        if (colon < 0) return defaultVal;
        int start = json.indexOf("\"", colon + 1);
        if (start < 0) return defaultVal;
        int end = json.indexOf("\"", start + 1);
        if (end < 0) return defaultVal;
        return json.substring(start + 1, end);
    }

    static double parseDouble(String json, String key, double defaultVal) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx < 0) return defaultVal;
        int colon = json.indexOf(":", idx + search.length());
        if (colon < 0) return defaultVal;
        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.')) end++;
        try { return Double.parseDouble(json.substring(start, end)); } catch (NumberFormatException e) { return defaultVal; }
    }

    static boolean parseBool(String json, String key, boolean defaultVal) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx < 0) return defaultVal;
        int colon = json.indexOf(":", idx + search.length());
        if (colon < 0) return defaultVal;
        String rest = json.substring(colon + 1).trim();
        if (rest.startsWith("true"))  return true;
        if (rest.startsWith("false")) return false;
        return defaultVal;
    }

    static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("\t", "\\t");
    }
}
