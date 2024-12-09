FROM node:20.10.0-alpine

WORKDIR /app

COPY package*.json /app/
COPY vite.config.ts /app/
COPY tsconfig.* /app/
COPY .env /app/
COPY .env.production /app/
COPY src/ /app/src
COPY public/ /app/public
COPY index.html /app/

RUN npm ci --only-production --silent
RUN npm run build-only

EXPOSE 5173

CMD ["npm", "run", "preview:prod", "--", "--port", "5173", "--host", "0.0.0.0"]
