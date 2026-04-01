FROM node:22 AS frontend-build
WORKDIR /frontend


COPY ./package.json ./package-lock.json ./
RUN npm install
COPY ./ ./
RUN rm -f .env.production
RUN rm -f .env.production.local
RUN rm -f .env.local
RUN rm -f .env
RUN npm run build

FROM nginx

ARG OPENAI_KEY
ARG ANTHROPIC_KEY
ARG AWS_BEDROCK_KEY
ARG AWS_REGION

#COPY conf/nginx.default.conf /etc/nginx/conf.d/default.conf
#COPY --from=frontend-build /frontend/dist /usr/share/nginx/html/msc-viewer


COPY conf/nginx.default.conf /etc/nginx/conf.d/default.__conf
RUN envsubst '${OPENAI_KEY} ${ANTHROPIC_KEY} ${AWS_REGION} ${AWS_BEDROCK_KEY} ' < /etc/nginx/conf.d/default.__conf > /etc/nginx/conf.d/default.conf
RUN cat /etc/nginx/conf.d/default.conf
RUN pwd

COPY --from=frontend-build /frontend/dist /usr/share/nginx/html/msc-viewer