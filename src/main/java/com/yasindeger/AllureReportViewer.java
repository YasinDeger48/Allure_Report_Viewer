package com.yasindeger;

import org.zeroturnaround.zip.ZipUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class AllureReportViewer {

    public static void main(String[] args) {

        String OSName = System.getProperty("os.name");
        System.out.println(OSName);
        JFrame frame = new JFrame("Allure Report Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);
        frame.setLayout(new FlowLayout());

        JTextField filePathField = new JTextField(20);
        JButton browseButton = new JButton("Browse");
        JButton showButton = new JButton("Show");

        frame.add(filePathField);
        frame.add(browseButton);
        frame.add(showButton);

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String zipFilePath = filePathField.getText();
                if (!zipFilePath.isEmpty()) {
                    try {
                        File zipFile = new File(zipFilePath);
                        String zipFileName = zipFile.getName();
                        String baseName = zipFileName.substring(0, zipFileName.lastIndexOf('.'));
                        File parentDir = zipFile.getParentFile();
                        File tempDir = new File(parentDir, baseName);
                        if (!tempDir.exists()) {
                            tempDir.mkdir();
                        }

                        ZipUtil.unpack(zipFile, tempDir);

                        String osName = System.getProperty("os.name").toLowerCase();
                        String command;
                        if (osName.contains("win")) {
                            // Windows için komut
                            command = "cmd.exe allure serve " + tempDir.getAbsolutePath()+"/allure-results";
                            System.out.println("bana bu komut lazım  ===> " + command);
                        } else if (osName.contains("mac")) {
                            // MAC için komut
                            command = "allure serve " + tempDir.getAbsolutePath()+"/allure-results";
                            System.out.println("bana bu komut lazım  ===> " + command);
                        }else {
                            // Unix tabanlı sistemler (Linux, MacOS) için komut
                            command = "sh -c allure serve " + tempDir.getAbsolutePath()+"/allure-results";
                            System.out.println("bana bu komut lazım  ===> " + command);

                        }
                        //command = "allure serve " + tempDir.getAbsolutePath()+"/allure-results";
                        //System.out.println("command = " + command);
                        Process process = Runtime.getRuntime().exec(command);

                        new Thread(() -> {
                            try {
                                int exitCode = process.waitFor();
                                if (exitCode == 0) {
                                    System.out.println("Allure report is being served.");
                                } else {
                                    System.out.println("Failed to serve allure report.");
                                }
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }).start();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a zip file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }
}
