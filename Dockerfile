FROM node:18.16.1

WORKDIR /app

COPY . .

RUN npm install -g npm@9.5.1
RUN npm install -g @angular/cli@17.0.9
RUN npm install

RUN ng build --configuration production

FROM nginx:latest

COPY --from=0 /app/dist/globitokuki /usr/share/nginx/html

COPY --from=0 /app/default.conf /etc/nginx/conf.d/default.conf
