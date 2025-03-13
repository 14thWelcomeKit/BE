package com.likelion13th.Welcomekit_BE.service;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {

	public void generateQR(HttpServletResponse response, Long id) {
		String qrUrl = "http://localhost:8080/api/attendance/success?sessionId=" + id;

		int width = 300;
		int height = 300;

		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(qrUrl, BarcodeFormat.QR_CODE, width, height);
			BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

			response.setContentType("image/png");
			OutputStream outputStream = response.getOutputStream();
			ImageIO.write(qrImage, "png", outputStream);

			outputStream.flush();
			outputStream.close();

		} catch (Exception e) {
			throw new RuntimeException("QR 코드 생성 중 오류 발생", e);
		}
	}
}
