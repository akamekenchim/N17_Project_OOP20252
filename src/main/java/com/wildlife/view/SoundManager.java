package com.wildlife.view;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.media.AudioClip;
import java.net.URL;

public class SoundManager {
    // Thư mục chứa âm thanh (nhớ tạo thư mục sounds ngang hàng với images trong resources)
    private static final String SOUND_PATH = "/sounds/";
    
    // Dùng Map để cache âm thanh, tránh việc load lại cùng một file nhiều lần gây tốn RAM
    private static Map<String, AudioClip> soundMap = new HashMap<>();

    public static AudioClip loadSound(String fileName) {
        // Kiểm tra xem âm thanh đã được load vào bộ nhớ chưa
        if (soundMap.containsKey(fileName)) {
            return soundMap.get(fileName);
        }
        
        try {
            String FULL_SOUND_PATH = SOUND_PATH + fileName;
            
            // Khác với Image dùng InputStream, AudioClip của JavaFX cần một đường dẫn URL dạng String
            URL resourceUrl = SoundManager.class.getResource(FULL_SOUND_PATH);
            
            if (resourceUrl == null) {
                throw new Exception("File not found: " + FULL_SOUND_PATH);
            }
            
            // Tạo đối tượng AudioClip từ đường dẫn
            AudioClip clip = new AudioClip(resourceUrl.toExternalForm());
            
            // Lưu vào Map để dùng cho các lần sau
            soundMap.put(fileName, clip);
            return clip;
            
        } catch (Exception e) {
            System.out.println("Ko tim thay am thanh: " + fileName);
            // Với âm thanh, ta khó tạo một "âm thanh mặc định" (như tạo ảnh pixel xen kẽ), 
            // nên cách tốt nhất là trả về null và bắt lỗi khi gọi hàm play()
            return null; 
        }
    }
    
    // Thêm một hàm tiện ích để gọi phát âm thanh nhanh chóng
    public static void playSound(String fileName) {
        AudioClip clip = loadSound(fileName);
        if (clip != null) {
            clip.play(); // Mặc định phát âm thanh 1 lần
        }
    }
}

