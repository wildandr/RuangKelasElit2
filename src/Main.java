import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Main {
    private static InputReader in;
    private static PrintWriter out;
    static School school = new School();

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int M = in.nextInt();
        for (int i = 0; i < M; i++) {
            int Mi = in.nextInt();
            school.addNewClass(Mi);
        }

        int Q = in.nextInt();
        for (int i = 0; i < Q; i++) {
            char query = in.nextChar();

            switch (query) {
                case 'T':
                    int poin = in.nextInt();
                    int idSiswa = in.nextInt();
                    school.giveTask(poin, idSiswa);
                    break;
                case 'C':
                    int idSiswaC = in.nextInt();
                    school.warnStudent(idSiswaC);
                    break;
                case 'G':
                    char direction = in.nextChar();
                    school.move(direction);
                    break;
                case 'S':
                    school.evaluate();
                    break;
                case 'K':
                    school.reorderClasses();
                    break;
                case 'A':
                    int N = in.nextInt();
                    school.addNewStudent(N);
                    break;
                default:
                    break;
            }
        }

        out.close();
    }
}

class School {
    private int numberOfClasses;
    private AVLTree[] classes;
    private LinkedList[] tutorList;
    private int currentClassIndex;

    public School() {
        this.numberOfClasses = 0;
        this.classes = new AVLTree[100001]; // Batasan maksimum kelas
        this.tutorList = new LinkedList[100001]; // Batasan maksimum kelas
        this.currentClassIndex = -1;
    }

    public void addNewClass(int Mi) {
        this.numberOfClasses++;
        this.classes[numberOfClasses] = new AVLTree();
        this.tutorList[numberOfClasses] = new LinkedList();

        for (int i = 1; i <= Mi; i++) {
            this.classes[numberOfClasses].root = this.classes[numberOfClasses].insert(this.classes[numberOfClasses].root, i, 0);
        }
    }

    public void giveTask(int poin, int idSiswa) {
        Node student = this.classes[currentClassIndex].root.searchNode(idSiswa);
        if (student != null) {
            student.points += poin;
            int tutorId = tutorList[currentClassIndex].getTutorId(idSiswa);
            if (tutorId != -1) {
                Node tutor = this.classes[currentClassIndex].root.searchNode(tutorId);
                tutor.points = Math.min(tutor.points + student.points - poin, 1000);
            }
        } else {
            System.out.println("-1");
        }
    }

    public void warnStudent(int idSiswa) {
        Node student = this.classes[currentClassIndex].root.searchNode(idSiswa);
        if (student != null) {
            if (student.points == 0) {
                System.out.println("0");
            } else {
                int warnings = student.incrementWarnings();
                if (warnings == 2) {
                    int newClassIndex = this.classes.length - 1;
                    if (this.numberOfClasses > 1) {
                        newClassIndex = (currentClassIndex + 1) % numberOfClasses;
                    }
                    Node tutor = this.classes[currentClassIndex].root.searchNode(student.tutorId);
                    this.classes[currentClassIndex].root = this.classes[currentClassIndex].delete(this.classes[currentClassIndex].root, idSiswa);
                    this.classes[newClassIndex].root = this.classes[newClassIndex].insert(this.classes[newClassIndex].root, idSiswa, 0);
                    tutorList[currentClassIndex].remove(idSiswa);
                    tutorList[newClassIndex].add(idSiswa);

                    currentClassIndex = newClassIndex;
                    System.out.println(currentClassIndex + 1);
                } else if (warnings == 3) {
                    this.classes[currentClassIndex].root = this.classes[currentClassIndex].delete(this.classes[currentClassIndex].root, idSiswa);
                    tutorList[currentClassIndex].remove(idSiswa);
                    System.out.println(idSiswa);
                } else {
                    System.out.println("-1");
                }
            }
        } else {
            System.out.println("-1");
        }
    }

    public void move(char direction) {
        if (direction == 'R') {
            currentClassIndex = (currentClassIndex + 1) % numberOfClasses;
        } else {
            currentClassIndex = (currentClassIndex - 1 + numberOfClasses) % numberOfClasses;
        }
        System.out.println(currentClassIndex + 1);
    }

    public void evaluate() {
        Node[] top3Best = this.classes[currentClassIndex].root.getTop3Best();
        Node[] top3Worst = this.classes[currentClassIndex].root.getTop3Worst();

        System.out.println(top3Best[0].key + " " + top3Best[1].key);
        System.out.println(top3Worst[0].key + " " + top3Worst[1].key + " " + top3Worst[2].key);
    }

    public void reorderClasses() {
        int prevIndex = currentClassIndex - 1;
        int nextIndex = currentClassIndex + 1;
        if (prevIndex < 0) prevIndex = numberOfClasses - 1;
        if (nextIndex >= numberOfClasses) nextIndex = 0;

        Node[] top3BestM = this.classes[currentClassIndex].root.getTop3Best();
        Node[] top3WorstM = this.classes[currentClassIndex].root.getTop3Worst();
        Node[] top3BestMA = this.classes[prevIndex].root.getTop3Best();
        Node[] top3WorstMA = this.classes[prevIndex].root.getTop3Worst();
        Node[] top3BestMB = this.classes[nextIndex].root.getTop3Best();
        Node[] top3WorstMB = this.classes[nextIndex].root.getTop3Worst();

        if (currentClassIndex == 0 && prevIndex == numberOfClasses - 1) {
            // Tukar 3 siswa terbaik pada M dengan 3 siswa terburuk pada M_A
            for (int i = 0; i < 3; i++) {
                swapStudents(top3BestM[i].key, top3WorstMA[i].key);
            }
        } else if (currentClassIndex == numberOfClasses - 1 && nextIndex == 0) {
            // Tukar 3 siswa terburuk pada M dengan 3 siswa terbaik pada M_B
            for (int i = 0; i < 3; i++) {
                swapStudents(top3WorstM[i].key, top3BestMB[i].key);
            }
        } else {
            // Tukar 3 siswa terbaik pada M dengan 3 siswa terburuk pada M_A
            for (int i = 0; i < 3; i++) {
                swapStudents(top3BestM[i].key, top3WorstMA[i].key);
            }
            // Tukar 3 siswa terburuk pada M dengan 3 siswa terbaik pada M_B
            for (int i = 0; i < 3; i++) {
                swapStudents(top3WorstM[i].key, top3BestMB[i].key);
            }
        }
    }

    private void swapStudents(int id1, int id2) {
        Node student1 = this.classes[currentClassIndex].root.searchNode(id1);
        Node student2 = this.classes[currentClassIndex].root.searchNode(id2);

        this.classes[currentClassIndex].root = this.classes[currentClassIndex].delete(this.classes[currentClassIndex].root, id1);
        this.classes[currentClassIndex].root = this.classes[currentClassIndex].delete(this.classes[currentClassIndex].root, id2);

        this.classes[currentClassIndex].root = this.classes[currentClassIndex].insert(this.classes[currentClassIndex].root, id1, student2.points);
        this.classes[currentClassIndex].root = this.classes[currentClassIndex].insert(this.classes[currentClassIndex].root, id2, student1.points);
    }

    public void addNewStudent(int N) {
        for (int i = 0; i < N; i++) {
            this.classes[currentClassIndex].root = this.classes[currentClassIndex].insert(this.classes[currentClassIndex].root, this.classes[currentClassIndex].root.getNextId(), 0);
            tutorList[currentClassIndex].add(this.classes[currentClassIndex].root.getNextId());
        }
        System.out.println(currentClassIndex + 1);
    }
}


class Node {
    public int tutorId;
    public int height;
    public Node next;
    public int data;
    Node root;
    // TODO: modify attributes as needed
    int key;
    int points;
    int warnings;
    Node left, right;

    Node(int key, int points) {
        this.key = key;
        this.points = points;
        this.left = this.right = null;
    }

    public Node searchNode(int idSiswa) {
        if (key == this.key) {
            return this;
        } else if (key < this.key && this.left != null) {
            return this.left.searchNode(key);
        } else if (key > this.key && this.right != null) {
            return this.right.searchNode(key);
        }
        return null;
    }

    int incrementWarnings() {
        warnings++;
        return warnings;
    }

    public Node[] getTop3Best() {
        Node[] result = new Node[3];
        getTopNodes(root, result, 0, true);
        return result;
    }

    private int getTopNodes(Node node, Node[] result, int index, boolean isAscending) {
        if (node == null || index >= 3) {
            return index;
        }

        if (isAscending) {
            index = getTopNodes(node.right, result, index, true);
            result[index++] = node;
            return getTopNodes(node.left, result, index, true);
        } else {
            index = getTopNodes(node.left, result, index, false);
            result[index++] = node;
            return getTopNodes(node.right, result, index, false);
        }
    }

    public Node[] getTop3Worst() {
        Node[] result = new Node[3];
        getTopNodes(root, result, 0, false);
        return result;
    }

    public int getNextId() {
        return getNextId(root);
    }

    private int getNextId(Node node) {
        if (node == null) {
            return 1; // ID dimulai dari 1
        }

        int leftId = getNextId(node.left);
        int rightId = getNextId(node.right);

        return Math.max(leftId, rightId) + 1;
    }
}

class AVLTree {
    Node root;

    Node insert(Node node, int key, int points) {
        if (node == null) {
            return new Node(key, points);
        }

        if (key < node.key) {
            node.left = insert(node.left, key, points);
        } else if (key > node.key) {
            node.right = insert(node.right, key, points);
        } else {
            // Duplicate key, do nothing (assuming ID is unique)
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && key < node.left.key) {
            return singleRightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && key > node.right.key) {
            return singleLeftRotate(node);
        }

        // Left Right Case
        if (balance > 1 && key > node.left.key) {
            node.left = singleLeftRotate(node.left);
            return singleRightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && key < node.right.key) {
            node.right = singleRightRotate(node.right);
            return singleLeftRotate(node);
        }

        return node;
    }

    Node delete(Node node, int key) {
        if (node == null) {
            return node;
        }

        if (key < node.key) {
            node.left = delete(node.left, key);
        } else if (key > node.key) {
            node.right = delete(node.right, key);
        } else {
            // Node with only one child or no child
            if ((node.left == null) || (node.right == null)) {
                Node temp = null;
                if (temp == node.left) {
                    temp = node.right;
                } else {
                    temp = node.left;
                }

                // No child case
                if (temp == null) {
                    temp = node;
                    node = null;
                } else {
                    // One child case
                    node = temp; // Copy the contents of the non-empty child
                }
            } else {
                // Node with two children: Get the inorder successor (smallest in the right subtree)
                Node temp = minValueNode(node.right);

                // Copy the inorder successor's data to this node
                node.key = temp.key;
                node.points = temp.points;

                // Delete the inorder successor
                node.right = delete(node.right, temp.key);
            }
        }

        if (node == null) {
            return node;
        }

        // Update height of current node
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Get the balance factor of this node to check whether this node became unbalanced
        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && getBalance(node.left) >= 0) {
            return singleRightRotate(node);
        }

        // Left Right Case
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = singleLeftRotate(node.left);
            return singleRightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && getBalance(node.right) <= 0) {
            return singleLeftRotate(node);
        }

        // Right Left Case
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = singleRightRotate(node.right);
            return singleLeftRotate(node);
        }

        return node;
    }

    Node singleLeftRotate(Node node) {
        Node newRoot = node.right;
        Node temp = newRoot.left;

        // Perform rotation
        newRoot.left = node;
        node.right = temp;

        // Update heights
        node.height = 1 + Math.max(height(node.left), height(node.right));
        newRoot.height = 1 + Math.max(height(newRoot.left), height(newRoot.right));

        return newRoot;
    }

    Node singleRightRotate(Node node) {
        Node newRoot = node.left;
        Node temp = newRoot.right;

        // Perform rotation
        newRoot.right = node;
        node.left = temp;

        // Update heights
        node.height = 1 + Math.max(height(node.left), height(node.right));
        newRoot.height = 1 + Math.max(height(newRoot.left), height(newRoot.right));

        return newRoot;
    }

    int height(Node node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    int getBalance(Node node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    Node[] getTop3Best() {
        Node[] result = new Node[3];
        getTopNodes(root, result, 0, true);
        return result;
    }

    Node[] getTop3Worst() {
        Node[] result = new Node[3];
        getTopNodes(root, result, 0, false);
        return result;
    }

    private int getTopNodes(Node node, Node[] result, int index, boolean isAscending) {
        if (node == null || index >= 3) {
            return index;
        }

        if (isAscending) {
            index = getTopNodes(node.right, result, index, true);
            result[index++] = node;
            return getTopNodes(node.left, result, index, true);
        } else {
            index = getTopNodes(node.left, result, index, false);
            result[index++] = node;
            return getTopNodes(node.right, result, index, false);
        }
    }
}


class LinkedList {
    private Node head;

    public LinkedList() {
        this.head = null;
    }

    public void add(int data) {
        Node newNode = new Node(data, 0); // Sesuaikan dengan parameter konstruktor Node Anda
        newNode.next = head;
        head = newNode;
    }

    public boolean contains(int data) {
        Node current = head;
        while (current != null) {
            if (current.data == data) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void remove(int data) {
        if (head == null) {
            return;
        }

        if (head.data == data) {
            head = head.next;
            return;
        }

        Node current = head;
        while (current.next != null && current.next.data != data) {
            current = current.next;
        }

        if (current.next != null) {
            current.next = current.next.next;
        }
    }

    public int getTutorId(int studentId) {
        Node current = head;
        while (current != null) {
            if (current.data == studentId) {
                return current.tutorId;
            }
            current = current.next;
        }
        return -1; // Jika tutor tidak ditemukan
    }
}


class InputReader {
    public BufferedReader reader;
    public StringTokenizer tokenizer;

    public InputReader(InputStream stream) {
        reader = new BufferedReader(new InputStreamReader(stream), 32768);
        tokenizer = null;
    }

    public String next() {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) {
            try {
                tokenizer = new StringTokenizer(reader.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return tokenizer.nextToken();
    }

    public char nextChar() {
        return next().charAt(0);
    }

    public int nextInt() {
        return Integer.parseInt(next());
    }
}
