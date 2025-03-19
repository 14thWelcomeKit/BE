import axios from "axios";
import { Cookies } from "react-cookie";

const cookies = new Cookies(); // ✅ 전역에서 쿠키 인스턴스 생성

const axiosInstance = axios.create({
  baseURL: "https://welcomekitbe.lion.it.kr/api",
});

// 요청 인터셉터 (Authorization 헤더 자동 추가)
axiosInstance.interceptors.request.use(
  async (config) => {
    const accessToken = cookies.get("accessToken"); // ✅ 전역 쿠키 인스턴스에서 가져옴

    if (accessToken) {
      console.log("🔹 토큰이 정상적으로 감:", accessToken); // ✅ 디버깅용 로그 추가
      config.headers["Authorization"] = `Bearer ${accessToken}`;
    } else {
      console.warn(
        "⚠️ 토큰이 없음. 인증 요청이 정상적으로 동작하지 않을 수 있음."
      );
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default axiosInstance;
