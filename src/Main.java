import java.io.*;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
public class Main {
    private static InputReader in;
    private static PrintWriter out;
    static School school;

    public static void main(String[] args) throws FileNotFoundException {
        // Menggunakan file sebagai input
        InputStream inputStream = new FileInputStream("/Users/owwl/Downloads/input.txt"); // Ini input pake path input txt karena buat debugging.
        OutputStream outputStream = System.out;                                                 // aslinya wajib pake CLI dan inputnya input reader
        in = new InputReader(inputStream);
        out = new PrintWriter(outputStream);

        school = new School();

        Integer M = tryParseInt(in.next());
        if (M == null) {
            out.println("Input tidak valid atau tidak lengkap 1.");
            out.close();
            return;
        }

        for (int i = 0; i < M; i++) {
            Integer Mi = tryParseInt(in.next());
            if (Mi == null) {
                out.println("Input tidak valid atau tidak lengkap 2.");
                out.close();
                return;
            }
            school.addNewClass(Mi);
        }

        Integer Q = tryParseInt(in.next());
        if (Q == null) {
            out.println("Input tidak valid atau tidak lengkap 3.");
            out.close();
            return;
        }

        for (int i = 0; i < Q; i++) {
            String queryTypeStr = in.next();
            if (queryTypeStr == null || queryTypeStr.length() == 0) {
                out.println("Input tidak valid atau tidak lengkap 4.");
                out.close();
                return;
            }
            char queryType = queryTypeStr.charAt(0);

            out.println("queryTypeStr: " + queryTypeStr + ", queryType: " + queryType); // Debugging output

            switch (queryType) {
                case 'T': // Tugas
                    int points = in.nextInt();
                    int studentId = in.nextInt();
                    school.giveTask(points, studentId);
                    break;
                case 'C': // Peringatan
                    int studentIdToWarn = in.nextInt();
                    school.warnStudent(studentIdToWarn);
                    break;
                case 'G': // Pindah Kelas
                    char direction = in.nextChar();
                    school.move(direction);
                    break;
                case 'S': // Evaluasi
                    school.evaluate();
                    break;
                case 'K': // Reorder
                    school.reorderClasses();
                    break;
                case 'A': // Tambah Siswa
                    int N = in.nextInt();
                    school.addNewStudent(N);
                    break;
                default:
//                    out.println("Perintah tidak dikenal: " + queryType);
                    break;
            }
        }

        out.close();
    }

    private static Integer tryParseInt(String value) {
        try {
            return value != null ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

class School {
    private AVLTree[] classes;
    private LinkedList[] tutorLists;
    private int numberOfClasses;
    private int currentClassIndex;

    public School() {
        this.classes = new AVLTree[100001]; // Asumsikan jumlah maksimum kelas adalah 100000
        this.tutorLists = new LinkedList[100001];
        this.numberOfClasses = 0;
        this.currentClassIndex = -1;
    }

    public void addNewClass(int numberOfStudents) {
        AVLTree newClass = new AVLTree();
        LinkedList newTutorList = new LinkedList();
        for (int i = 1; i <= numberOfStudents; i++) {
            newClass.insert(i, 0); // Misalkan semua siswa dimulai dengan 0 poin
            newTutorList.add(i); // Setiap siswa adalah tutor bagi diri mereka sendiri pada awalnya
        }
        this.classes[numberOfClasses] = newClass;
        this.tutorLists[numberOfClasses] = newTutorList;
        currentClassIndex = numberOfClasses; // Update currentClassIndex ke kelas yang baru ditambahkan
        numberOfClasses++;
    }


    public void giveTask(int points, int studentId) {
        Node student = classes[currentClassIndex].search(studentId);
        if (student != null) {
            student.addPoints(points);
        } else {
//            System.out.println("Siswa dengan ID " + studentId + " tidak ditemukan di kelas saat ini.");
        }
    }

    public void warnStudent(int studentId) {
        Node student = classes[currentClassIndex].search(studentId);
        if (student != null) {
            student.addWarning();
            if (student.warnings == 3) {
                removeStudentFromClass(studentId);
                System.out.println(studentId);
            }
        } else {
//            System.out.println("Siswa dengan ID " + studentId + " tidak ditemukan di kelas saat ini.");
        }
    }

    private void removeStudentFromClass(int studentId) {
        classes[currentClassIndex].delete(studentId);
        tutorLists[currentClassIndex].remove(studentId);
    }

    public void move(char direction) {
        if (numberOfClasses <= 0) {
//            System.out.println("Tidak ada kelas yang tersedia untuk dipindahkan.");
            return;
        }
        if (direction == 'L') {
            currentClassIndex = (currentClassIndex - 1 + numberOfClasses) % numberOfClasses;
        } else if (direction == 'R') {
            currentClassIndex = (currentClassIndex + 1) % numberOfClasses;
        } else {
            System.out.println("Arah tidak valid. Gunakan 'L' untuk kiri atau 'R' untuk kanan.");
        }
    }


    public void evaluate() {
        if (currentClassIndex == -1 || classes[currentClassIndex] == null) {
//            System.out.println("Tidak ada kelas yang dipilih saat ini.");
            return;
        }

        Node bestStudent = classes[currentClassIndex].getHighestPointStudent();
        Node worstStudent = classes[currentClassIndex].getLowestPointStudent();

        if (bestStudent != null && worstStudent != null) {
//            System.out.println("Siswa dengan poin tertinggi: " + bestStudent.key + " (" + bestStudent.points + " poin)");
//            System.out.println("Siswa dengan poin terendah: " + worstStudent.key + " (" + worstStudent.points + " poin)");
        } else {
//            System.out.println("Tidak dapat mengevaluasi kelas saat ini.");
        }
    }


    public void reorderClasses() {
        // Sort kelas berdasarkan rata-rata poin
        // Ini bisa dilakukan dengan berbagai cara, tergantung pada struktur data yang Anda gunakan
        // Contoh di bawah ini adalah pendekatan sederhana dan tidak efisien

        for (int i = 0; i < numberOfClasses - 1; i++) {
            for (int j = 0; j < numberOfClasses - i - 1; j++) {
                if (classes[j].getAveragePoints() > classes[j + 1].getAveragePoints()) {
                    // Swap kelas
                    AVLTree tempClass = classes[j];
                    classes[j] = classes[j + 1];
                    classes[j + 1] = tempClass;

                    LinkedList tempTutorList = tutorLists[j];
                    tutorLists[j] = tutorLists[j + 1];
                    tutorLists[j + 1] = tempTutorList;
                }
            }
        }
    }


    public void addNewStudent(int numberOfNewStudents) {
        if (currentClassIndex < 0 || currentClassIndex >= numberOfClasses) {
//            System.out.println("Kelas tidak valid saat ini.");
            return;
        }
        for (int i = 0; i < numberOfNewStudents; i++) {
            int newStudentId = classes[currentClassIndex].getMaxId() + 1;
            classes[currentClassIndex].insert(newStudentId, 0);
            tutorLists[currentClassIndex].add(newStudentId);
        }
    }


    // Metode tambahan yang mungkin diperlukan
}

class Node {
    int key; // ID siswa
    int points; // Poin siswa
    int warnings; // Jumlah peringatan
    Node left, right; // Untuk AVL Tree
    int height; // Untuk AVL Tree balancing
    Node next; // Untuk LinkedList
    int tutorId; // ID tutor, jika ada

    // Konstruktor untuk AVL Tree
    public Node(int key, int points) {
        this.key = key;
        this.points = points;
        this.left = null;
        this.right = null;
        this.height = 1; // Tinggi awal untuk node baru dalam AVL Tree
        this.warnings = 0; // Tidak ada peringatan pada awalnya
    }

    // Konstruktor untuk LinkedList
    public Node(int tutorId, Node next) {
        this.tutorId = tutorId;
        this.next = next;
    }

    // Metode untuk memperbarui ketinggian node
    public void updateHeight() {
        this.height = Math.max(height(left), height(right)) + 1;
    }

    // Metode bantuan untuk mendapatkan ketinggian node
    public static int height(Node node) {
        return (node == null) ? 0 : node.height;
    }

    // Metode untuk mendapatkan keseimbangan node dalam AVL Tree
    public int getBalance() {
        return height(left) - height(right);
    }

    // Metode untuk menambah peringatan kepada siswa
    public void addWarning() {
        this.warnings++;
    }

    // Metode untuk menambah poin kepada siswa
    public void addPoints(int additionalPoints) {
        this.points += additionalPoints;
    }

    // Metode bantuan untuk mencetak data node (opsional, berguna untuk debugging)
    @Override
    public String toString() {
        return "Node{" +
                "key=" + key +
                ", points=" + points +
                ", warnings=" + warnings +
                ", height=" + height +
                ", tutorId=" + tutorId +
                '}';
    }
}

class AVLTree {
    Node root;
    public Node getHighestPointStudent() {
        Node current = root;
        while (current != null && current.right != null) {
            current = current.right;
        }
        return current;
    }
    public Node getLowestPointStudent() {
        Node current = root;
        while (current != null && current.left != null) {
            current = current.left;
        }
        return current;
    }
    public double getAveragePoints() {
        int[] sumAndCount = getSumAndCount(root);
        if (sumAndCount[1] == 0) return 0.0;
        return (double) sumAndCount[0] / sumAndCount[1];
    }

    private int[] getSumAndCount(Node node) {
        if (node == null) {
            return new int[]{0, 0};
        }
        int[] left = getSumAndCount(node.left);
        int[] right = getSumAndCount(node.right);
        int sum = left[0] + right[0] + node.points;
        int count = left[1] + right[1] + 1;
        return new int[]{sum, count};
    }
    public int getMaxId() {
        // Asumsikan bahwa nilai ID tertinggi adalah di node paling kanan
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.key;
    }


    // Metode untuk memasukkan node baru
    public void insert(int key, int points) {
        root = insertRec(root, key, points);
    }

    // Metode rekursif untuk memasukkan node
    private Node insertRec(Node node, int key, int points) {
        // Langkah 1: melakukan operasi insert standar BST
        if (node == null) {
            return new Node(key, points);
        }

        if (key < node.key) {
            node.left = insertRec(node.left, key, points);
        } else if (key > node.key) {
            node.right = insertRec(node.right, key, points);
        } else {
            // Duplikat key tidak diizinkan
            return node;
        }

        // Langkah 2: Perbarui tinggi node yang baru dimasukkan
        node.updateHeight();

        // Langkah 3: Dapatkan faktor keseimbangan untuk mengecek apakah node menjadi tidak seimbang
        int balance = node.getBalance();

        // Jika node menjadi tidak seimbang, maka ada 4 kasus

        // Kasus Left Left
        if (balance > 1 && key < node.left.key) {
            return rightRotate(node);
        }

        // Kasus Right Right
        if (balance < -1 && key > node.right.key) {
            return leftRotate(node);
        }

        // Kasus Left Right
        if (balance > 1 && key > node.left.key) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Kasus Right Left
        if (balance < -1 && key < node.right.key) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        // Kembalikan node yang telah berubah (dalam kasus rotasi) atau node yang sama (jika tidak ada rotasi)
        return node;
    }

    // Metode untuk rotasi kanan
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Lakukan rotasi
        x.right = y;
        y.left = T2;

        // Perbarui tinggi
        y.updateHeight();
        x.updateHeight();

        // Kembalikan node baru
        return x;
    }

    // Metode untuk rotasi kiri
    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Lakukan rotasi
        y.left = x;
        x.right = T2;

        // Perbarui tinggi
        x.updateHeight();
        y.updateHeight();

        // Kembalikan node baru
        return y;
    }

    // Metode untuk menghapus node
    public void delete(int key) {
        root = deleteRec(root, key);
    }

    // Metode rekursif untuk menghapus node
    private Node deleteRec(Node root, int key) {
        // Langkah 1: melakukan operasi delete standar BST
        if (root == null) {
            return root;
        }

        // Jika key yang akan dihapus lebih kecil dari key root, maka berada di subtree kiri
        if (key < root.key) {
            root.left = deleteRec(root.left, key);
        }
        // Jika key yang akan dihapus lebih besar dari key root, maka berada di subtree kanan
        else if (key > root.key) {
            root.right = deleteRec(root.right, key);
        }
        // Jika key sama dengan key root, maka node ini yang akan dihapus
        else {
            // Node dengan satu anak atau tanpa anak
            if ((root.left == null) || (root.right == null)) {
                Node temp = null;
                if (temp == root.left) {
                    temp = root.right;
                } else {
                    temp = root.left;
                }

                // Tidak ada anak
                if (temp == null) {
                    temp = root;
                    root = null;
                } else { // Satu anak
                    root = temp; // Copy the contents of the non-empty child
                }
            } else {
                // Node dengan dua anak: Dapatkan inorder successor (node terkecil di subtree kanan)
                Node temp = minValueNode(root.right);

                // Copy data inorder successor ke node ini
                root.key = temp.key;

                // Hapus inorder successor
                root.right = deleteRec(root.right, temp.key);
            }
        }

        // Jika hanya ada satu node
        if (root == null) {
            return root;
        }

        // Langkah 2: Perbarui tinggi node saat ini
        root.updateHeight();

        // Langkah 3: Dapatkan keseimbangan node untuk mengecek apakah node ini menjadi tidak seimbang
        int balance = root.getBalance();

        // Jika node ini menjadi tidak seimbang, maka ada 4 kasus

        // Kasus Left Left
        if (balance > 1 && root.left.getBalance() >= 0) {
            return rightRotate(root);
        }

        // Kasus Left Right
        if (balance > 1 && root.left.getBalance() < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        // Kasus Right Right
        if (balance < -1 && root.right.getBalance() <= 0) {
            return leftRotate(root);
        }

        // Kasus Right Left
        if (balance < -1 && root.right.getBalance() > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    // Metode bantuan untuk mencari node dengan nilai minimal
    Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    // Metode untuk mencari node berdasarkan key
    public Node search(int key) {
        return searchRec(root, key);
    }

    // Metode rekursif untuk pencarian
    private Node searchRec(Node root, int key) {
        if (root == null || root.key == key) {
            return root;
        }

        if (root.key > key) {
            return searchRec(root.left, key);
        }

        return searchRec(root.right, key);
    }
}


class LinkedList {
    private Node head;

    public LinkedList() {
        this.head = null;
    }

    // Metode untuk menambahkan node baru ke awal list
    public void add(int tutorId) {
        Node newNode = new Node(tutorId, this.head);
        this.head = newNode;
    }

    // Metode untuk menghapus node dengan tutorId tertentu
    public void remove(int tutorId) {
        if (head == null) {
            return;
        }

        // Jika tutorId ada di kepala list
        if (head.tutorId == tutorId) {
            head = head.next;
            return;
        }

        Node current = head;
        while (current.next != null) {
            if (current.next.tutorId == tutorId) {
                current.next = current.next.next;
                return;
            }
            current = current.next;
        }
    }

    // Metode untuk mencari node dengan tutorId tertentu
    public Node find(int tutorId) {
        Node current = head;
        while (current != null) {
            if (current.tutorId == tutorId) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    // Metode untuk mendapatkan ID tutor dari siswa tertentu
    public int getTutorId(int studentId) {
        Node current = head;
        while (current != null) {
            if (current.tutorId == studentId) {
                return current.tutorId;
            }
            current = current.next;
        }
        return -1; // Jika tutor tidak ditemukan
    }

    // Metode bantuan untuk mencetak list (opsional, berguna untuk debugging)
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            sb.append(current.tutorId).append(" -> ");
            current = current.next;
        }
        sb.append("null");
        return sb.toString();
    }
}


class InputReader {
    private BufferedReader reader;
    private StringTokenizer tokenizer;

    public InputReader(InputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream), 32768);
        tokenizer = null;
    }

    // Metode untuk mendapatkan string berikutnya dari input
    public String next() {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    return null; // Atau anda bisa melempar exception khusus jika diperlukan
                }
                tokenizer = new StringTokenizer(line);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return tokenizer.nextToken();
    }


    // Metode untuk mendapatkan integer berikutnya dari input
    public int nextInt() {
        return Integer.parseInt(next());
    }

    // Metode untuk mendapatkan karakter berikutnya dari input
    public char nextChar() {
        String result = next();
        if (result == null || result.length() == 0) {
            throw new NoSuchElementException("Tidak ada lebih banyak elemen untuk dibaca");
        }
        return result.charAt(0);
    }


    // Metode untuk mendapatkan double berikutnya dari input
    public double nextDouble() {
        return Double.parseDouble(next());
    }

    // Metode untuk mendapatkan long berikutnya dari input
    public long nextLong() {
        return Long.parseLong(next());
    }

    // Metode untuk membaca seluruh baris berikutnya dari input
    public String nextLine() {
        String str = "";
        try {
            str = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return str;
    }

    // Metode untuk menutup reader
    public void close() throws IOException {
        reader.close();
    }
}
