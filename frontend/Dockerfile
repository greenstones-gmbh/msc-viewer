FROM node:18 AS frontend-build
WORKDIR /frontend


COPY ./package.json ./package-lock.json ./
RUN npm install
COPY ./ ./
RUN npm run build

FROM nginx

COPY conf/nginx.default.conf /etc/nginx/conf.d/default.conf
#COPY build /usr/share/nginx/html/msc-viewer
COPY --from=frontend-build /frontend/build /usr/share/nginx/html/msc-viewer