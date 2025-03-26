const { createProxyMiddleware } = require("http-proxy-middleware");

module.exports = function (app) {
  app.use(
    "/msc-viewer/api",
    createProxyMiddleware({
      target: "http://localhost:8080/api",
      changeOrigin: true,
      secure: false,
    })
  );
};
