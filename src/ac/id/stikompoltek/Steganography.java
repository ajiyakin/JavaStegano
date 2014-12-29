package ac.id.stikompoltek;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Steganography {

	/*
	 * Steganography Konstruktor kosong
	 */
	public Steganography() {
	}

	/*
	 * Enkripsi gambar dengan teks, hasilnya berupa file gambar berekstensi *.png
	 * 
	 * @param path Alamat folder gambar (path) yang akan dimodifikasi (dimasuki pesan)
	 * 
	 * @param original Nama dari gambar yang akan dimodifikasi (dimasuki pesan)
	 * 
	 * @param ext1 Type ekstensi dari gambar yang akan dimodifikasi (dimasukki pesan) -> jpg, png
	 * 
	 * @param stegan Nama file hasil penyisipan pesan (gambar hasil)
	 * 
	 * @param message Pesan teks yang akan disembunyikan digambar
	 * 
	 * @param type integer merepresentasikan salah satu dari encoding, basic atau advance
	 */
	public boolean encode(String path, String original, String ext1,
			String stegan, String message) {
		String file_name = image_path(path, original, ext1);
		BufferedImage image_orig = getImage(file_name);

		// user space is not necessary for Encrypting
		BufferedImage image = user_space(image_orig);
		image = add_text(image, message);

		return (setImage(image, new File(image_path(path, stegan, "png")),
				"png"));
	}

	/*
	 * Dekripsi mengasumsikan gambar yang digunakan adalah *.png
	 * Menggali pesan dari sebuah gambar
	 * 
	 * @param path Alamat folder dari gambar yang mengandung pesan yang akan didecode
	 * 
	 * @param name Nama dari gambar yang akan digali pesannya
	 * 
	 * @param type integer Merepresentasikan tipe encode-nya, basic atau advance
	 */
	public String decode(String path, String name) {
		byte[] decode;
		try {
			// user space is necessary for decrypting
			BufferedImage image = user_space(getImage(image_path(path, name,
					"png")));
			decode = decode_text(get_byte_data(image));
			return (new String(decode));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"There is no hidden message in this image!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return "";
		}
	}

	/*
	 * Mengembalikan alamat (path) komplit dari file, dengan format : path\nama_file.ext
	 * 
	 * @param path Alamat (path) dari folder file
	 * 
	 * @param name Nama file
	 * 
	 * @param ext Extensi file
	 * 
	 * @return Mengembalikan string dari alamat(path) file
	 */
	private String image_path(String path, String name, String ext) {
		return path + "/" + name + "." + ext;
	}

	/*
	 * Method get untuk mengambil gambar yang sedang diolah
	 * 
	 * @param f Alamat (path) komplit dari gambar
	 * 
	 * @return Mengembalikan gambar (buffer / BufferedImage) yang diambil dari path yang diberikan
	 * 
	 * @see Steganography.image_path
	 */
	private BufferedImage getImage(String f) {
		BufferedImage image = null;
		File file = new File(f);

		try {
			image = ImageIO.read(file);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Image could not be read!",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}

	/*
	 * Method set untuk menyimpan file gambar
	 * 
	 * @param image File gambar yang hendak disimpan
	 * 
	 * @param file File dimana gambar tersebut akan disimpan 
	 * 
	 * @param ext Ekstensi dari gambar yang akan disimpan menjadi file
	 * 
	 * @return Mengembalikan true jika berhasil simpan gambar di file yang diinginkan
	 */
	private boolean setImage(BufferedImage image, File file, String ext) {
		try {
			file.delete(); // delete resources used by the File
			ImageIO.write(image, ext, file);
			return true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "File could not be saved!",
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	/*
	 * Bertanggung jawab menambahkan text kedalam gambar
	 * 
	 * @param image Gambar yang akan ditambahi text
	 * 
	 * @param text Text yang akan disisipkan kedalam gambar
	 * 
	 * @return Mengembalikan gambar dengan teks yang telah disisipkan didalamnya
	 */
	private BufferedImage add_text(BufferedImage image, String text) {
		// convert all items to byte arrays: image, message, message length
		byte img[] = get_byte_data(image);
		byte msg[] = text.getBytes();
		byte len[] = bit_conversion(msg.length);
		try {
			encode_text(img, len, 0); // 0 first positiong
			encode_text(img, msg, 32); // 4 bytes of space for length:
										// 4bytes*8bit = 32 bits
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Target File cannot hold message!", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}

	/*
	 * 
	 * Membuat ruang atau tempat untuk gambar. Untuk editing dan saving dalam bentuk byte
	 * 
	 * @param image Gambar yang akan dimasukkan kedalam ruang yang telah disediakan, dan hapus kembali ruang yang tadi disediakan
	 * 
	 * @return Mengembalikan ruang yang telah diisi gambar
	 */
	private BufferedImage user_space(BufferedImage image) {
		// create new_img with the attributes of image
		BufferedImage new_img = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D graphics = new_img.createGraphics();
		graphics.drawRenderedImage(image, null);
		graphics.dispose(); // release all allocated memory for this image
		return new_img;
	}

	/*
	 * Mengambil gambar dalam bentu array yang berisi byte dari gambar tersebut
	 * 
	 * @param image Gambar yang ingin diambil byte-nya
	 * 
	 * @return Mengembalikan array berisi byte dari gambar yang dimasukkan
	 * 
	 * @see Raster
	 * 
	 * @see WritableRaster
	 * 
	 * @see DataBufferByte
	 */
	private byte[] get_byte_data(BufferedImage image) {
		WritableRaster raster = image.getRaster();
		DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
		return buffer.getData();
	}

	/*
	 * Meng-generate format byte dari sebuah integer
	 * 
	 * @param i Integer yang ingin diambil nilai byte-nya
	 * 
	 * @return Mengembalikan array berisi byte[4] yang dikonversi dari integer yang dimasukkan
	 * bytes
	 */
	private byte[] bit_conversion(int i) {
		// originally integers (ints) cast into bytes
		// byte byte7 = (byte)((i & 0xFF00000000000000L) >>> 56);
		// byte byte6 = (byte)((i & 0x00FF000000000000L) >>> 48);
		// byte byte5 = (byte)((i & 0x0000FF0000000000L) >>> 40);
		// byte byte4 = (byte)((i & 0x000000FF00000000L) >>> 32);

		// only using 4 bytes
		byte byte3 = (byte) ((i & 0xFF000000) >>> 24); // 0
		byte byte2 = (byte) ((i & 0x00FF0000) >>> 16); // 0
		byte byte1 = (byte) ((i & 0x0000FF00) >>> 8); // 0
		byte byte0 = (byte) ((i & 0x000000FF));
		// {0,0,0,byte0} is equivalent, since all shifts >=8 will be 0
		return (new byte[] { byte3, byte2, byte1, byte0 });
	}

	/*
	 * Meng-encode sebuat array yang berisi byte kedalam array bytes yang diinginkan (diinputkan)
	 * 
	 * @param image Data array dari sebuah gambar
	 * 
	 * @param array tambahan dari data yang ingin ditambahkan kedalam data array gambar
	 * 
	 * @param offset Offset array gambar untuk menambahkan data tambahan
	 * 
	 * @return Mengembalikan data array dari hasil array gambar dan array data tambahan
	 */
	private byte[] encode_text(byte[] image, byte[] addition, int offset) {
		// check that the data + offset will fit in the image
		if (addition.length + offset > image.length) {
			throw new IllegalArgumentException("File not long enough!");
		}
		// loop through each addition byte
		for (int i = 0; i < addition.length; ++i) {
			// loop through the 8 bits of each byte
			int add = addition[i];
			for (int bit = 7; bit >= 0; --bit, ++offset) // ensure the new
															// offset value
															// carries on
															// through both
															// loops
			{
				// assign an integer to b, shifted by bit spaces AND 1
				// a single bit of the current byte
				int b = (add >>> bit) & 1;
				// assign the bit by taking: [(previous byte value) AND 0xfe] OR
				// bit to add
				// changes the last bit of the byte in the image to be the bit
				// of addition
				image[offset] = (byte) ((image[offset] & 0xFE) | b);
			}
		}
		return image;
	}

	/*
	 * Menggali text tersembunyi dari gambar
	 * 
	 * @param image Data array, merepresentasikan sebuah gambar
	 * 
	 * @return Mengembalikan data array yang mengandung text tersembunyi
	 */
	private byte[] decode_text(byte[] image) {
		int length = 0;
		int offset = 32;
		// loop through 32 bytes of data to determine text length
		for (int i = 0; i < 32; ++i) // i=24 will also work, as only the 4th
										// byte contains real data
		{
			length = (length << 1) | (image[i] & 1);
		}

		byte[] result = new byte[length];

		// loop through each byte of text
		for (int b = 0; b < result.length; ++b) {
			// loop through each bit within a byte of text
			for (int i = 0; i < 8; ++i, ++offset) {
				// assign bit: [(new byte value) << 1] OR [(text byte) AND 1]
				result[b] = (byte) ((result[b] << 1) | (image[offset] & 1));
			}
		}
		return result;
	}
}