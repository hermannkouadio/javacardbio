/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wonder.javacard.fingerprint;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JPanel;

class ImagePanel extends JPanel {
    //private PlanarImage image;

    private BufferedImage buffImage = null;

    void drawFingerImage(int nWidth, int nHeight, byte[] buff) throws IOException {
        buffImage = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_BYTE_GRAY);
        buffImage.getRaster().setDataElements(0, 0, nWidth, nHeight, buff);

        Graphics g = buffImage.createGraphics();
        //g.drawImage(buffImage, 0, 0, nWidth, nHeight, null);
        g.drawImage(buffImage, 0, 0, this.getWidth(), this.getHeight(), this);
        g.dispose();
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(buffImage, 0, 0, this);
    }

    public ImagePanel() {
        super();
        this.setBounds(new Rectangle(260, 17, 270, 310));
        this.setLayout(null);
    }
}
