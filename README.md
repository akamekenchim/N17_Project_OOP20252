Flow (tạm thời) của chương trình:
SimEngine có pt Start - đây chính là game loop. AnimationTimer có hàm handle(long now)
sẽ làm 3 việc chính: Xóa toàn bộ màn hình, duyệt qua toàn bộ các thực thể được cất trong
WorldMap, render chúng lên màn hình.

Việc duyệt đó chính là gọi hàm Update cùa class WorldMap, hàm đó sẽ gọi hàm update của từng thực
thể. Hàm renderEntities cũng tương tự, nó sẽ duyệt và gọi hàm render của từng thực thể.

Hàm update của từng thực thể:
Gọi hàm bên AI xin trả về vector hướng di chuyển (gồm 2 con số (dx, dy))
Công thức tính quãng đường di chuyển: x += dx*delta*speed ----- y += dy*delta*speed
Gọi hàm bên WorldMap xem là có di chuyển được về hướng đó không, nếu không, gọi lại hàm AI yêu cầu hướng di chuyển khác
hoặc có thể quy định luôn: sẽ đi chếch trái 15-20 độ so với hướng ko đi đc, để nếu có nhiều vật cản thì con vật sẽ xoay đủ 
360 độ tìm lối đi hợp lí
(cái này làm sau)

Nếu di chuyển được, cộng thực tiếp x và y như công thức trên vào tọa độ x và y của thực thể.
Tham khảo hàm render t có viết sẵn.

AI: (đã giải thích trên file)

Bấm 1 + chuột trái để tạo cỏ
Bấm 2 + chuột trái để tạo con vật ăn cỏ (Aggressive) (mới chỉ test)
Bấm 3 + Chuột trái để tạo con vật ăn thịt (mới chỉ test)
Bấm Spacebar để tạm dừng mô phỏng
Có thể dùng lăn chuột kết hợp kéo chuột trái để Panning/Zooming
Bấm mũi tên lên / xuống để tăng tốc/giảm tốc cho mô phỏng
