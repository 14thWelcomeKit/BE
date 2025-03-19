# 1. React 빌드 단계
FROM node:18 AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build

# 2. 빌드된 정적 파일을 serve로 제공
FROM node:18
WORKDIR /app
COPY --from=build /app/build /app
RUN npm install -g serve
CMD ["serve", "-s", "/app", "-l", "3000"]

EXPOSE 3000
