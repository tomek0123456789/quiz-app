# Stage 1: Build the React application
FROM node:25.2.1-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
EXPOSE 5173
CMD ["npm", "start"]
