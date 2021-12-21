
package gamebanco;

import java.awt.Graphics;
import java.util.ArrayList;

class DiemCuoi {
    public static int TargetFx, TargetFy; // Tọa độ của đích để chiếc xe hướng đến
    public static boolean DichConOrKhong = true; // Đích ban đầu là có,
}

public class ThuatToan {
    private Node start; // Nút Bắt đầu = tọa độ xe
    private Node target; // Nút Kết thúc

    private Node trunggian; // Các nút trung gian này có nhiệm vụ tìm con từ start - > target
    private Node trunggian1;
    private Node[][] nodeList; // Này là mảng 2 chiều Node tương ứng với 1 ô trong game.

    public ThuatToan(int StartFx, int StartFy) {
        initThuatToan(StartFx, StartFy);
    }

    public void ChayThuatToan() {
        calculatePath();
        TimCon();
    }

    public void initThuatToan(int StartFx, int StartFy) { // Khởi tạo thuật toán
        nodeList = new Node[DoRongDai.countH][DoRongDai.countW];
        for (int i = 0; i < DoRongDai.countH; i++)
            for (int j = 0; j < DoRongDai.countW; j++) {
                nodeList[i][j] = new Node(i, j).setX(i * DoRongDai.countH).setY(j * DoRongDai.countW);

            }
        start = nodeList[StartFx][StartFy];
        target = nodeList[DiemCuoi.TargetFx][DiemCuoi.TargetFy];
        for (int i = 0; i < DoRongDai.countH; i++)
            for (int j = 0; j < DoRongDai.countW; j++) {
                nodeList[i][j].calcH(target, 1);
                if (DoRongDai.b[i][j] == 1) {
                    nodeList[i][j].setWall(true);
                } else
                    nodeList[i][j].setWall(false);
            }

        trunggian = start;
    }

    public void TimCon() { // ngược lại với tìm cha, thì bây giờ tìm con
        trunggian = target;
        trunggian1 = target;
        while (trunggian1 != start) {
            trunggian1 = trunggian.getParent();
            trunggian1.setChilden(trunggian);
            trunggian = trunggian1;
        }
        trunggian = start; // trả lại trung gian = start ban đầu
    }

    public void clear() { // hàm này sẽ xóa toàn bộ những dữ liệu đã có trong nodeList, để trở lại ban đầu
        start = null;
        target = null;
        trunggian = null;
        for (int i = 0; i < DoRongDai.countH; i++)
            for (int j = 0; j < DoRongDai.countW; j++)
                nodeList[i][j].clear();
    }

    public void resetNode() {
        target = null;
        start = null;
        for (int i = 0; i < DoRongDai.countH; i++)
            for (int j = 0; j < DoRongDai.countW; j++) {
                nodeList[i][j] = new Node(i, j).setX(i * DoRongDai.cellW).setY(j * DoRongDai.cellH);
            }
    }

    // ĐÂY LÀ THUẬT TOÁN A* MÀ TÔI NGÀY ĐÊM NGHIÊN CỨU == > MINH BK ....(GHI NHỚ
    // CÔNG ƠN TÁC GIẢ : MINH BK)...

    // CHÚ Ý: Trong game thì posW tính theo chiêu ngang, posH tính theo chiều dọc
    // Còn với mảng, nodeList[i][j] , b[i][j] thì i tính theo chiều dọc, j tính theo
    // chiều ngang...khó nhai r
    public void calculatePath() {

        ArrayList<Node> closed = new ArrayList<Node>();
        ArrayList<Node> open = new ArrayList<Node>();
        Node currentNode = start;
        open.add(currentNode);

        while (open.isEmpty() != true) {
            open.remove(currentNode);
            closed.add(currentNode);
            if (currentNode == target)
                break;

            for (int i = currentNode.getFX() - 1; i <= currentNode.getFX() + 1; i++) { // nghĩa là các giá trị cận
                                                                                       // kề,theo chiều dọc
                if (i < 0 || i > DoRongDai.countH - 1) { // nếu <0 và > 20 thì bỏ qua
                    continue;
                }
                for (int j = currentNode.getFY() - 1; j <= currentNode.getFY() + 1; j++) { // nghĩa là các giá trị cận
                                                                                           // kề, theo chiều ngang
                    if (j < 0 || j > DoRongDai.countW - 1) {
                        continue;
                    }

                    if (i != currentNode.getFX() && j != currentNode.getFY()) {
                        continue; // Loại bỏ các nút chéo với currentNode
                    }
                    if (i == currentNode.getFX() && j == currentNode.getFY()) { // loại bỏ các nút chính nó
                        continue;
                    }
                    if (nodeList[j][i].isWall()) // cái này bên trên đã chú ý rồi nhé,i,j ở trong tọa độ game và
                                                 // nodeList là nghịch đảo của nhau.
                        continue;
                    Node node = nodeList[i][j];

                    if (!open.contains(node) && !closed.contains(node)) {
                        node.setG(node.calcG(currentNode)); // cấp nhập G cho node
                        node.setF(node.getF()); // cập nhập F cho node
                        node.setParent(currentNode);
                        open.add(node);
                    } else if (open.contains(node)) {
                        if (node.getG() > node.calcG(currentNode)) {
                            node.setG(node.calcG(currentNode));
                            node.setF(node.getF());
                            node.setParent(currentNode);
                        }
                    } else if (closed.contains(node)) {
                        if (node.getG() > node.calcG(currentNode)) {
                            node.setG(node.calcG(currentNode));
                            node.setF(node.getF());
                            node.setParent(currentNode);
                            closed.remove(node);
                            open.add(node);
                        }
                    }

                }

            }
            currentNode = minOfList(open);
        }
    }

    // Hàm tìm nút có giá trị F nhỏ nhất trong danh sách
    public Node minOfList(ArrayList<Node> open) {

        Node n = open.get(0);
        for (Node node : open) {
            if (node.getF() < n.getF()) {
                n = node;
            }
        }
        return n;
    }

    public Node getStart() {
        return start;
    }

    public Node getTarget() {
        return target;
    }

    public Node getTrunggian() {
        return trunggian;
    }

    public Node getTrunggian1() {
        return trunggian1;
    }

    public void setStart(Node start) {
        this.start = start;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public void setTrunggian(Node trunggian) {
        this.trunggian = trunggian;
    }

    public void setTrunggian1(Node trunggian1) {
        this.trunggian1 = trunggian1;
    }

    public void setNodeList(Node[][] nodeList) {
        this.nodeList = nodeList;
    }

    public Node[][] getNodeList() {
        return nodeList;
    }
}
