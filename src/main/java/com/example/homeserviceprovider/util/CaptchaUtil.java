package com.example.homeserviceprovider.util;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.GradiatedBackgroundProducer;
import cn.apiclub.captcha.noise.CurvedLineNoiseProducer;
import cn.apiclub.captcha.text.producer.DefaultTextProducer;
import cn.apiclub.captcha.text.renderer.DefaultWordRenderer;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public interface CaptchaUtil {

    static Captcha createCaptcha(int width, int height) {
        return new Captcha.Builder(width, height)
                .addBackground(new GradiatedBackgroundProducer())
                .addText(new DefaultTextProducer(), new DefaultWordRenderer())
                .addNoise(new CurvedLineNoiseProducer()).build();
    }

    static String encodeBase64(Captcha captcha) {
        String image = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(captcha.getImage(), "png", outputStream);
            byte[] arr = Base64.getEncoder().encode(outputStream.toByteArray());
            image = new String(arr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
