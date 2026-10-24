FROM node:21-alpine AS build
WORKDIR /build
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

FROM nginx:latest-alpine
COPY --from=build /build/dist /usr/share/nginx/html
COPY frontend/nginx/default.conf /etc/nginx/confi.d/default.conf
EXPOSE 80
CMD [ "nginx", "-g", "daemon off;" ]