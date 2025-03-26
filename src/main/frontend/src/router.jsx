import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Main from "./pages/main";
import MyPage from "./pages/mypage";
import Check from "./pages/check";
import Bingo from "./pages/bingo";
import Setting from "./pages/setting";
import Introduce from "./pages/introduce";
import Login from "./pages/login";
import SignUp from "./pages/sign";
import QRCodeScanner from "./pages/qrscanner";
import ChangePassword from "./pages/ChangePassword";

const RouterComponent = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Setting />} />
        <Route path="/main" element={<Main />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/check" element={<Check />} />
        <Route path="/bingo" element={<Bingo />} />
        <Route path="/introduce" element={<Introduce />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="/qrcodescanner" element={<QRCodeScanner />} />
        <Route path="/change-password" element={<ChangePassword />} />
      </Routes>
    </BrowserRouter>
  );
};

export default RouterComponent;
