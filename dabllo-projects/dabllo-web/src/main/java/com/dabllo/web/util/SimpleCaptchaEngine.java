package com.dabllo.web.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.ImageFilter;

import org.springframework.stereotype.Component;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import com.octo.captcha.component.image.color.RandomRangeColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.LineTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;

/**
 * @author mixueqiang
 * @since Aug 1, 2016
 */
@SuppressWarnings("deprecation")
@Component("com.dabllo.web.util.simpleCaptchaEngine")
public class SimpleCaptchaEngine extends ListImageCaptchaEngine {

  private static final int MIN_WORD_LENGTH = 4;
  private static final int MAX_WORD_LENGTH = 5;
  private static final int MIN_FONT_SIZE = 18;
  private static final int MAX_FONT_SIZE = 20;
  private static final int IMAGE_WIDTH_SIZE = 100;
  private static final int IMAGE_HEIGHT_SIZE = 34;

  @Override
  protected void buildInitialFactories() {
    FontGenerator fontGenerator = new RandomFontGenerator(MIN_FONT_SIZE, MAX_FONT_SIZE,
        new Font[] { new Font("nyala", Font.BOLD, MAX_FONT_SIZE), new Font("Bell MT", Font.PLAIN, MAX_FONT_SIZE), new Font("Credit valley", Font.BOLD, MIN_FONT_SIZE) });
    BackgroundGenerator backgroundGenerator = new UniColorBackgroundGenerator(IMAGE_WIDTH_SIZE, IMAGE_HEIGHT_SIZE, Color.white);

    RandomRangeColorGenerator randomRangeColorGenerator = new RandomRangeColorGenerator(new int[] { 20, 200 }, new int[] { 30, 210 }, new int[] { 20, 200 });
    LineTextDecorator lineTextDecorator = new LineTextDecorator(1, randomRangeColorGenerator); // 曲线干扰
    TextDecorator[] textDecorators = new TextDecorator[1];
    textDecorators[0] = lineTextDecorator;
    TextPaster textPaster = new DecoratedRandomTextPaster(MIN_WORD_LENGTH, MAX_WORD_LENGTH,
        new RandomListColorGenerator(new Color[] { new Color(23, 170, 27), new Color(220, 34, 11), new Color(23, 67, 172) }), textDecorators);

    ImageDeformation postDef = new ImageDeformationByFilters(new ImageFilter[] {});
    ImageDeformation backDef = new ImageDeformationByFilters(new ImageFilter[] {});
    ImageDeformation textDef = new ImageDeformationByFilters(new ImageFilter[] {});

    WordGenerator wordGenerator = new RandomWordGenerator("0123456789abcdefghijklmnopqrstuvwxyz");
    WordToImage word2image = new DeformedComposedWordToImage(fontGenerator, backgroundGenerator, textPaster, backDef, textDef, postDef);
    addFactory(new GimpyFactory(wordGenerator, word2image));
  }

}
