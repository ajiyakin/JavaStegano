package ac.id.stikompoltek;

import java.io.File;

public class Image_Filter extends javax.swing.filechooser.FileFilter {
	/*
	 * Hanya pilih file yang berkestensi *.jpg dan/atau *.png (file gambar)
	 * 
	 * @param ext Ekstensi file yang dipilih
	 * 
	 * @return Mengembalikan true jika ekstensi file yang dipilih adalah *.jpg atau *.png
	 */
	protected boolean isImageFile(String ext) {
		return (ext.equals("jpg") || ext.equals("png"));
	}

	/*
	 * Periksa direktori file dan ekstensi file yang diperbolehkan
	 * 
	 * @param f File yang akan diperiksa
	 *
	 * @return Mengembalikan true jika File adalah direktori yang valid dan ekstensinya diperbolehkan
	 */
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		if (extension.equals("jpg") || extension.equals("png")) {
			return true;
		}
		return false;
	}

	/*
	 * Mengambil pesan deskripsi file (apakah disupport atau tidak)
	 * 
	 * @return Mengembalikan deskripsi pesan dari file yang terpilih
	 */
	public String getDescription() {
		return "Supported Image Files";
	}

	/*
	 * Menentukan ekstensi file yang dipilih
	 * 
	 * @param f File yang ingin diperiksa ekstensinya
	 * 
	 * @return Mengembalikan pesan (berupa String) representasi dari ekstensi file tersebut. Misal : .jpg atau .png
	 */
	protected static String getExtension(File f) {
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1)
			return s.substring(i + 1).toLowerCase();
		return "";
	}
}
