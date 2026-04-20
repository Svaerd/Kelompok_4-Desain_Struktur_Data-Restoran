import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.Scanner;

// Enum untuk Kategori Layanan
enum KategoriLayanan {
    DAPUR_UTAMA, MINUMAN, DESSERT
}

// Enum untuk Status Item
enum StatusItem {
    MENUNGGU, DIPROSES, DITUNDA, SELESAI
}

// 1. STACK: Digunakan untuk menyimpan histori perubahan status (LIFO)
class HistoriStatus {
    StatusItem status;
    String waktu; // Dalam praktiknya gunakan LocalDateTime

    public HistoriStatus(StatusItem status, String waktu) {
        this.status = status;
        this.waktu = waktu;
    }

    @Override
    public String toString() {
        return status + " (" + waktu + ")";
    }
}

// Representasi Item dalam Pesanan
class ItemPesanan {
    String kodeItem;
    String nama;
    KategoriLayanan kategori;
    Stack<HistoriStatus> historiStatus; // STACK untuk histori

    public ItemPesanan(String kodeItem, String nama, KategoriLayanan kategori) {
        this.kodeItem = kodeItem;
        this.nama = nama;
        this.kategori = kategori;
        this.historiStatus = new Stack<>();
        updateStatus(StatusItem.MENUNGGU, "Sistem: Baru masuk");
    }

    public void updateStatus(StatusItem statusBaru, String waktu) {
        historiStatus.push(new HistoriStatus(statusBaru, waktu));
    }

    public StatusItem getStatusTerakhir() {
        return historiStatus.peek().status;
    }

    @Override
    public String toString() {
        return nama + " [" + kodeItem + "] - Status: " + getStatusTerakhir();
    }
}

// Representasi Satu Pesanan Lengkap
class Pesanan {
    String kodePesanan;
    // 2. LINKED LIST: Menjaga urutan item dalam satu pesanan
    LinkedList<ItemPesanan> daftarItem; 

    public Pesanan(String kodePesanan) {
        this.kodePesanan = kodePesanan;
        this.daftarItem = new LinkedList<>();
    }

    public void tambahItem(ItemPesanan item) {
        daftarItem.add(item);
    }
}

// Kelas Utama Pengelola Sistem Restoran
public class SistemRestoran {

    // 3. HASH MAP: Pencarian data cepat O(1) berdasarkan kode
    private Map<String, Pesanan> mapPesanan;
    private Map<String, ItemPesanan> mapSemuaItem;
    
    // 3. HASH MAP: Untuk menampilkan pesanan berdasarkan kategori layanan tertentu
    private Map<KategoriLayanan, LinkedList<Pesanan>> mapPesananPerKategori;

    // 4. QUEUE: Antrean item yang menunggu diproses (FIFO)
    private Queue<ItemPesanan> antreanDapurUtama;
    private Queue<ItemPesanan> antreanMinuman;
    private Queue<ItemPesanan> antreanDessert;
    private Queue<ItemPesanan> antreanDitunda; // Untuk bahan habis

    public SistemRestoran() {
        mapPesanan = new HashMap<>();
        mapSemuaItem = new HashMap<>();
        mapPesananPerKategori = new HashMap<>();
        
        // Inisialisasi map kategori
        for (KategoriLayanan kat : KategoriLayanan.values()) {
            mapPesananPerKategori.put(kat, new LinkedList<>());
        }

        antreanDapurUtama = new LinkedList<>();
        antreanMinuman = new LinkedList<>();
        antreanDessert = new LinkedList<>();
        antreanDitunda = new LinkedList<>();
    }

    // Fungsi menerima pesanan baru
    public void terimaPesananBaru(Pesanan pesanan) {
        mapPesanan.put(pesanan.kodePesanan, pesanan);

        for (ItemPesanan item : pesanan.daftarItem) {
            mapSemuaItem.put(item.kodeItem, item);
            
            // Masukkan pesanan ke map kategori agar mudah difilter nanti
            if (!mapPesananPerKategori.get(item.kategori).contains(pesanan)) {
                mapPesananPerKategori.get(item.kategori).add(pesanan);
            }

            // Masukkan ke Queue sesuai bagian (Route to specific queue)
            masukkanKeAntrean(item);
        }
    }

    private void masukkanKeAntrean(ItemPesanan item) {
        switch (item.kategori) {
            case DAPUR_UTAMA: antreanDapurUtama.offer(item); break;
            case MINUMAN: antreanMinuman.offer(item); break;
            case DESSERT: antreanDessert.offer(item); break;
        }
    }

    // Fungsi memproses item
    public void prosesItem(ItemPesanan item, String waktu) {
        item.updateStatus(StatusItem.DIPROSES, waktu);
        // Logika pengeluaran dari Queue akan dihandle di fungsi terpisah atau saat di pop
    }

    // Fungsi jika bahan habis
    public void tundaItemKarenaBahanHabis(ItemPesanan item, String waktu) {
        item.updateStatus(StatusItem.DITUNDA, waktu);
        antreanDitunda.offer(item); // Masuk queue antrean tertunda
        System.out.println("Item ditunda: " + item.nama);
    }

    // Pencarian Efisien dengan HashMap
    public void cariDataBerdasarkanKode(String kode) {
        System.out.println("\n--- Hasil Pencarian untuk kode: " + kode + " ---");
        if (mapPesanan.containsKey(kode)) {
            System.out.println("Ditemukan Pesanan dengan " + mapPesanan.get(kode).daftarItem.size() + " item.");
        } else if (mapSemuaItem.containsKey(kode)) {
            ItemPesanan item = mapSemuaItem.get(kode);
            System.out.println("Ditemukan Item: " + item.nama);
            System.out.println("Histori Perubahan Status (Paling atas = Terbaru):");
            // Menampilkan Stack secara terbalik (LIFO ke FIFO view untuk report)
            for (int i = item.historiStatus.size() - 1; i >= 0; i--) {
                System.out.println(" -> " + item.historiStatus.get(i));
            }
        } else {
            System.out.println("Data tidak ditemukan.");
        }
    }

    // Menampilkan pesanan berdasarkan kategori layanan
    public void tampilkanPesananBerdasarkanKategori(KategoriLayanan kategori) {
        System.out.println("\n--- Daftar Pesanan yang memiliki item " + kategori + " ---");
        LinkedList<Pesanan> daftar = mapPesananPerKategori.get(kategori);
        if(daftar.isEmpty()){
            System.out.println("Tidak ada pesanan.");
            return;
        }
        for (Pesanan p : daftar) {
            System.out.println("- Pesanan: " + p.kodePesanan);
        }
    }

    // Fungsi tambahan untuk melihat antrean saat ini
    public void tampilkanSemuaAntrean() {
        System.out.println("\n--- Antrean Saat Ini ---");
        System.out.println("Dapur Utama : " + antreanDapurUtama);
        System.out.println("Minuman     : " + antreanMinuman);
        System.out.println("Dessert     : " + antreanDessert);
        System.out.println("Ditunda     : " + antreanDitunda);
    }

    // MAIN METHOD UNTUK SIMULASI / TESTING (Diubah menjadi Interaktif)
    public static void main(String[] args) {
        SistemRestoran sistem = new SistemRestoran();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== SISTEM MANAJEMEN RESTORAN ===");
            System.out.println("1. Tambah Pesanan Baru");
            System.out.println("2. Lihat Antrean Berjalan");
            System.out.println("3. Update Status Item (Proses/Selesai)");
            System.out.println("4. Tunda Item (Bahan Habis)");
            System.out.println("5. Cari Data (Berdasarkan Kode)");
            System.out.println("6. Lihat Pesanan Berdasarkan Kategori");
            System.out.println("0. Keluar");
            System.out.print("Pilih menu: ");
            
            int pilihan = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (pilihan) {
                case 1:
                    System.out.print("Masukkan Kode Pesanan (cth: P-001): ");
                    String kodePesanan = scanner.nextLine();
                    Pesanan pesananBaru = new Pesanan(kodePesanan);
                    
                    boolean tambahItemLagi = true;
                    while (tambahItemLagi) {
                        System.out.print("Masukkan Kode Item (cth: ITM-101): ");
                        String kodeItem = scanner.nextLine();
                        System.out.print("Masukkan Nama Item: ");
                        String namaItem = scanner.nextLine();
                        
                        System.out.println("Pilih Kategori: 1. DAPUR UTAMA | 2. MINUMAN | 3. DESSERT");
                        System.out.print("Pilihan kategori: ");
                        int kat = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        
                        KategoriLayanan kategori = KategoriLayanan.DAPUR_UTAMA; // Default
                        if (kat == 2) kategori = KategoriLayanan.MINUMAN;
                        else if (kat == 3) kategori = KategoriLayanan.DESSERT;

                        pesananBaru.tambahItem(new ItemPesanan(kodeItem, namaItem, kategori));

                        System.out.print("Tambah item lagi di pesanan ini? (y/n): ");
                        if (!scanner.nextLine().equalsIgnoreCase("y")) {
                            tambahItemLagi = false;
                        }
                    }
                    sistem.terimaPesananBaru(pesananBaru);
                    System.out.println(">> Pesanan " + kodePesanan + " berhasil ditambahkan!");
                    break;

                case 2:
                    sistem.tampilkanSemuaAntrean();
                    break;

                case 3:
                    System.out.print("Masukkan Kode Item yang ingin diupdate: ");
                    String kodeUpdate = scanner.nextLine();
                    if (sistem.mapSemuaItem.containsKey(kodeUpdate)) {
                        ItemPesanan item = sistem.mapSemuaItem.get(kodeUpdate);
                        System.out.println("Pilih Status Baru: 1. DIPROSES | 2. SELESAI");
                        System.out.print("Pilihan: ");
                        int stat = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Masukkan Waktu (cth: 12:30): ");
                        String waktu = scanner.nextLine();
                        
                        item.updateStatus(stat == 1 ? StatusItem.DIPROSES : StatusItem.SELESAI, waktu);
                        System.out.println(">> Status item " + item.nama + " berhasil diperbarui!");
                    } else {
                        System.out.println(">> Error: Item tidak ditemukan!");
                    }
                    break;

                case 4:
                    System.out.print("Masukkan Kode Item yang bahan bakunya habis: ");
                    String kodeTunda = scanner.nextLine();
                    if (sistem.mapSemuaItem.containsKey(kodeTunda)) {
                        System.out.print("Masukkan Waktu (cth: 12:35): ");
                        String waktu = scanner.nextLine();
                        sistem.tundaItemKarenaBahanHabis(sistem.mapSemuaItem.get(kodeTunda), waktu);
                    } else {
                        System.out.println(">> Error: Item tidak ditemukan!");
                    }
                    break;

                case 5:
                    System.out.print("Masukkan Kode Pesanan / Kode Item: ");
                    String kodeCari = scanner.nextLine();
                    sistem.cariDataBerdasarkanKode(kodeCari);
                    break;

                case 6:
                    System.out.println("Pilih Kategori: 1. DAPUR UTAMA | 2. MINUMAN | 3. DESSERT");
                    System.out.print("Pilihan: ");
                    int katCari = scanner.nextInt();
                    scanner.nextLine();
                    
                    KategoriLayanan kategoriCari = KategoriLayanan.DAPUR_UTAMA;
                    if (katCari == 2) kategoriCari = KategoriLayanan.MINUMAN;
                    else if (katCari == 3) kategoriCari = KategoriLayanan.DESSERT;
                    
                    sistem.tampilkanPesananBerdasarkanKategori(kategoriCari);
                    break;

                case 0:
                    running = false;
                    System.out.println("Keluar dari sistem. Terima kasih!");
                    break;

                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
        scanner.close();
    }
}