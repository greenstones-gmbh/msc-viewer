import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { visualizer } from "rollup-plugin-visualizer";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => ({
  define: {
    __BUILD_TIME__: JSON.stringify(new Date().toISOString()),
  },
  plugins: [react()],
  css: {
    postcss: {
      plugins: [],
    },
  },
  server: {
    port: 3000,
    proxy: {
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
}));
