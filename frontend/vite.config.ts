import react from "@vitejs/plugin-react";
import { visualizer } from "rollup-plugin-visualizer";
import { defineConfig, loadEnv } from "vite";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  return {
    plugins: [react()],
    css: {
      postcss: {
        plugins: [],
      },
    },
    server: {
      port: 3000,
      proxy: {
        "/msc-viewer/api/anthropic": {
          target: "https://api.anthropic.com/v1",
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/msc-viewer\/api\/anthropic/, ""),
          configure: (proxy, options) => {
            proxy.on("proxyReq", (proxyReq, req) => {
              console.log("🔵 [Proxy Request]", {
                method: req.method,
                path: req.url,
                target: `${options.target}${proxyReq.path}`,
                timestamp: new Date().toISOString(),
              });

              proxyReq.setHeader("x-api-key", `${env.ANTHROPIC_KEY}`);
              proxyReq.setHeader(
                "anthropic-dangerous-direct-browser-access",
                "true",
              );
            });
          },
        },

        "/msc-viewer/api/openai": {
          target: "https://api.openai.com/v1",
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/msc-viewer\/api\/openai/, ""),
          configure: (proxy) => {
            proxy.on("proxyReq", (proxyReq) => {
              proxyReq.setHeader("Authorization", `Bearer ${env.OPENAI_KEY}`);
            });
          },
        },

        "/msc-viewer/api/bedrock": {
          target: `https://bedrock-runtime.${env.AWS_REGION || "us-east-1"}.amazonaws.com`,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/msc-viewer\/api\/bedrock/, ""),
          configure: (proxy, options) => {
            proxy.on("proxyReq", (proxyReq, req) => {
              console.log("🔵 [Proxy Request]", {
                method: req.method,
                path: req.url,
                target: `${options.target}${proxyReq.path}`,
                timestamp: new Date().toISOString(),
              });

              proxyReq.setHeader(
                "Authorization",
                `Bearer ${env.AWS_BEDROCK_KEY}`,
              );
            });
          },
        },

        "/msc-viewer/api": {
          target: "http://localhost:8080",
          changeOrigin: true,
          secure: false,
          rewrite: (path) => path.replace(/^\/msc-viewer/, ""),
        },
      },
    },
    base: "/msc-viewer/",
    build: {
      rollupOptions: {
        treeshake: true,

        // output: {
        //   manualChunks(id) {
        //     if (id.includes("node_modules/ol/")) {
        //       return "openlayers";
        //     }
        //     if (id.includes("node_modules/@neo4j-nvl/")) {
        //       return "neo4j-nvl";
        //     }
        //     if (id.includes("node_modules/cytoscape")) {
        //       return "graphlibs";
        //     }
        //   },
        // },
        plugins: mode === "analyze" ? [visualizer({ open: true })] : [],
      },
    },
  };
});
