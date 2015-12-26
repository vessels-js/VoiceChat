package net.gliby.gman;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <b>
 * <p>
 * Description:</b><br>
 * JIniFile stores and retrieves application-specific information and settings from INI files. <br>
 * <br>
 * JIniFile enables handling the storage and retrieval of application-specific information and settings in a standard
 * INI file. The INI file text format is a standard introduced in Windows 3.x for storing and retrieving application
 * settings from session to session. An INI file stores information in logical groupings, called "sections." For
 * example, the WIN.INI file contains a section called "[Desktop]". Within each section, actual data values are stored
 * in named keys. Keys take the form: <br>
 * <br>
 * &lt;keyname&gt;=&lt;value&gt;<br>
 * A FileName is passed to the JIniFile constructor and identifies the INI file that the object accesses.
 * </p>
 *
 * @author Andreas Norman
 * @version 1.0
 */
public class JINIFile extends ArrayList {
	public static class JINIReadException extends Exception {

		/**
		 * @param string
		 */
		public JINIReadException(String string) {
			super(string);
		}

	}

	private final File userFileName;

	/**
	 * Constructor JIniFile Creates a JIniFile object for an application. Create assigns the FileName parameter to the
	 * FileName property, which is used to specify the name of the INI file to use.
	 * <p>
	 * <b>Note:</b> By default the INI files are stored in the Application directory. To work with an INI file in
	 * another location, specify the full path name of the file in FileName.
	 */
	public JINIFile(File file) throws IOException {
		clear();
		this.userFileName = file;
		if (userFileName.exists()) {
			final BufferedReader inbuf = new BufferedReader(new FileReader(this.userFileName));
			while (true) {
				final String s = inbuf.readLine();
				if (s == null) {
					break;
				}
				if (!s.startsWith(";")) add(s);
			}
			inbuf.close();
		} else {
			file.createNewFile();
			final BufferedReader inbuf = new BufferedReader(new FileReader(this.userFileName));
			inbuf.close();
		}
	}

	/**
	 * addToList is used internally to add values to the INI file specified in FileName.
	 * <p>
	 * Section is the section in the INI file in which to search for the key.
	 * <p>
	 * key is the name of the key to search for.
	 * <p>
	 * value is the name of the value to write
	 * <p>
	 * <b>Note:</b> Attempting to write a data value to a nonexistent section or attempting to write data to a
	 * nonexistent key are not errors. In these cases, addToList creates the section and key and sets its initial value
	 * to value.
	 *
	 * @param Section
	 *            the section name
	 * @param key
	 *            the key name
	 * @param value
	 *            the value
	 */
	private void addToList(String Section, String key, String value) {
		if (this.SectionExist((Section))) {
			if (this.ValueExist(Section, key)) {
				final int pos = this.ValuePosition(Section, key);
				remove(pos);
				add(pos, value);
			} else {
				add((this.SectionPosition(Section) + 1), value);
			}
		} else {
			add("[" + Section + "]");
			add(value);
		}
	}

	/**
	 * Call DeleteKey to erase an INI file entry. Section is string containing the name of an INI file section, and key
	 * is a string containing the name of the key from for which to set a nil value.
	 * <p>
	 * <b>Note:</b> Attempting to delete a key in a nonexistent section or attempting to delete a nonexistent key are
	 * not errors. In these cases, DeleteKey does nothing.
	 *
	 * @param Section
	 *            the section name
	 * @param key
	 *            the key name
	 */
	public void DeleteKey(String Section, String key) {
		if (this.ValuePosition(Section, key) > 0) {
			remove(this.ValuePosition(Section, key));
		}
	}

	/**
	 * Call EraseSection to remove a section, all its keys, and their data values from an INI file. Section identifies
	 * the INI file section to remove.
	 *
	 * @param Section
	 *            the section name
	 */
	public void EraseSection(String Section) {
		String s;
		final int start = this.SectionPosition(Section) + 1;
		if (this.SectionPosition(Section) > -1) {
			for (int i = start; i < size(); i++) {
				s = get(i).toString();
				if (s.startsWith("[") && s.endsWith("]")) {
					break;
				} else {
					remove(i);
					i--;
				}
			}
			remove(this.SectionPosition(Section));
		}
	}

	/**
	 * Call Readbool to read a string value from an INI file. Section identifies the section in the file that contains
	 * the desired key. key is the name of the key from which to retrieve the value. defaultValue is the boolean value
	 * to return if the:<br>
	 * - Section does not exist.<br>
	 * - Key does not exist.<br>
	 * - Data value for the key is not assigned.<br>
	 *
	 * @param Section
	 *            the section name
	 * @param key
	 *            the key name
	 * @param defaultValue
	 *            default value if no value is found
	 * @return returns the value. If empty or nonexistent it will return the default value
	 * @throws JINIReadException
	 */
	public boolean ReadBool(String Section, String key, boolean defaultValue) throws JINIReadException {
		final String s = get(ValuePosition(Section, key)).toString().substring(key.length() + 1, get(ValuePosition(Section, key)).toString().length());
		boolean value = defaultValue;
		if (ValuePosition(Section, key) > 0) {
			value = Boolean.parseBoolean(s);
			return value;
		}
		throw new JINIReadException("ReadBool operation failed: " + s);
	}

	/**
	 * Call ReadFloat to read a string value from an INI file. Section identifies the section in the file that contains
	 * the desired key. key is the name of the key from which to retrieve the value. defaultValue is the float value to
	 * return if the:<br>
	 * - Section does not exist.<br>
	 * - Key does not exist.<br>
	 * - Data value for the key is not assigned.<br>
	 *
	 * @param Section
	 *            the section name
	 * @param key
	 *            the key name
	 * @param defaultValue
	 *            default value if no value is found
	 * @return returns the value. If empty or nonexistent it will return the default value
	 */
	public Float ReadFloat(String Section, String key, Float defaultValue) throws JINIReadException {
		Float value = new Float(0f);
		value = defaultValue;
		if (ValuePosition(Section, key) > 0) {
			final int strLen = key.length() + 1;
			value = Float.valueOf(get(ValuePosition(Section, key)).toString().substring(strLen, get(ValuePosition(Section, key)).toString().length()));
			return value;
		}
		throw new JINIReadException("ReadFloat operation failed.");
	}

	/**
	 * Call ReadInteger to read a string value from an INI file. Section identifies the section in the file that
	 * contains the desired key. key is the name of the key from which to retrieve the value. defaultValue is the
	 * integer value to return if the:<br>
	 * - Section does not exist.<br>
	 * - Key does not exist.<br>
	 * - Data value for the key is not assigned.<br>
	 *
	 * @param Section
	 *            the section name
	 * @param key
	 *            the key name
	 * @param defaultValue
	 *            default value if no value is found
	 * @return returns the value. If empty or nonexistent it will return the default value
	 */
	public int ReadInteger(String Section, String key, int defaultValue) throws JINIReadException {
		int value = defaultValue;
		if (ValuePosition(Section, key) > 0) {
			final int strLen = key.length() + 1;
			value = Integer.parseInt(get(ValuePosition(Section, key)).toString().substring(strLen, get(ValuePosition(Section, key)).toString().length()));
			return value;
		}
		throw new JINIReadException("ReadInteger operation failed.");
	}

	/**
	 * Call ReadSection to retrieve the names of all keys within a specified section of an INI file into an ArrayList
	 * object.
	 *
	 * @param Section
	 *            the section name
	 * @return an ArrayList holding all key names.
	 */
	public ArrayList ReadSection(String Section) {
		String s;
		final ArrayList myList = new ArrayList();
		final int start = this.SectionPosition(Section) + 1;
		if (this.SectionPosition(Section) > -1) {
			for (int i = start; i < size(); i++) {
				s = get(i).toString();
				if (s.startsWith("[") && s.endsWith("]")) {
					break;
				} else {
					myList.add(s.substring(0, s.indexOf("=")));
				}
			}
		}
		return myList;
	}

	/**
	 * Call ReadSections to retrieve the names of all sections in the INI file into an ArrayList object.
	 *
	 * @return an ArrayList holding the sections
	 */
	public ArrayList ReadSections() {
		final ArrayList list = new ArrayList();
		String s;
		for (int i = 0; i < size(); i++) {
			s = get(i).toString();
			if (s.startsWith("[") && s.endsWith("]")) {
				list.add(s.substring(1, (s.length() - 1)));
			}
		}
		return list;
	}

	/**
	 * Call ReadSectionValues to read the keys, and the values from all keys, within a specified section of an INI file
	 * into an ArrayList object. Section identifies the section in the file from which to read key values.
	 *
	 * @param Section
	 *            the section name
	 * @return an ArrayList holding the values in the specified section.
	 */
	public ArrayList ReadSectionValues(String Section) {
		final ArrayList myList = new ArrayList();
		final int start = this.SectionPosition(Section) + 1;
		String s;
		if (this.SectionPosition(Section) > -1) {
			for (int i = start; i < size(); i++) {
				s = get(i).toString().substring(get(i).toString().indexOf("=") + 1, get(i).toString().length());
				if (s.startsWith("[") && s.endsWith("]")) {
					break;
				} else {
					myList.add(s);
				}
			}
		}
		return myList;
	}

	/**
	 * Call ReadString to read a string value from an INI file. Section identifies the section in the file that contains
	 * the desired key. key is the name of the key from which to retrieve the value. defaultValue is the string value to
	 * return if the:<br>
	 * - Section does not exist.<br>
	 * - Key does not exist.<br>
	 * - Data value for the key is not assigned.<br>
	 *
	 * @param Section
	 *            the section name
	 * @param key
	 *            the key name
	 * @param defaultValue
	 *            default value if no value is found
	 * @return returns the value. If empty or nonexistent it will return the default value
	 */
	public String ReadString(String Section, String key, String defaultValue) {
		String value = defaultValue;
		if (ValuePosition(Section, key) > 0) {
			final int strLen = key.length() + 1;
			value = get(ValuePosition(Section, key)).toString().substring(strLen, get(ValuePosition(Section, key)).toString().length());
		} else {
			try {
				throw new Exception("Failed to parse");
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * Use SectionExists to determine whether a section exists within the INI file specified in FileName.
	 * <p>
	 * Section is the INI file section SectionExists determines the existence of.
	 * <p>
	 * SectionExists returns a Boolean value that indicates whether the section in question exists.
	 *
	 * @param Section
	 *            the section name
	 * @return returns <code>true</code> if the section exists.
	 */
	public boolean SectionExist(String Section) {
		String s;
		boolean val = false;
		for (int i = 0; i < size(); i++) {
			s = get(i).toString();
			if (s.equals("[" + Section + "]")) {
				val = true;
				break;
			}
		}
		return val;
	}

	/**
	 * SectionExists is used internally to determine the position of a section within the INI file specified in
	 * FileName.
	 * <p>
	 * Section is the INI file section SectionExists determines the position of.
	 * <p>
	 * SectionExists returns an Integer value that indicates the position of the section in question.
	 *
	 * @param Section
	 *            the section name
	 * @return will return the position of the section. Returns -1 not found.
	 */
	private int SectionPosition(String Section) {
		String s;
		int pos = -1;
		for (int i = 0; i < size(); i++) {
			s = get(i).toString();
			if (s.equals("[" + Section + "]")) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	/**
	 * Call UpdateFile to flush buffered reads from and writes to the INI file to disk.
	 *
	 * @return returns <code>true</code> if sucessful
	 */
	public boolean UpdateFile() {
		try {
			final BufferedWriter outbuf = new BufferedWriter(new FileWriter(this.userFileName, false));
			int i = 0;
			while (i < size()) {
				final String s = get(i).toString();
				if (s == null) {
					break;
				}
				outbuf.write(s);
				outbuf.newLine();
				i++;
			}
			outbuf.close();
			return true;
		} catch (final IOException ioe) {
			return false;
		}
	}

	/**
	 * Use ValueExist to determine whether a key exists in the INI file specified in FileName.<br>
	 * Section is the section in the INI file in which to search for the key.<br>
	 * key is the name of the key to search for.<br>
	 * ValueExist returns a Boolean value that indicates whether the key exists in the specified section.<br>
	 *
	 * @param Section
	 *            the section name
	 * @param key
	 *            the key name
	 * @return returns <code>true</code> if value exists
	 */
	public boolean ValueExist(String Section, String key) {
		final int start = SectionPosition(Section);
		String s;
		boolean val = false;
		key.length();
		for (int i = start + 1; i < size(); i++) {
			s = get(i).toString();
			if (s.startsWith(key + "=")) {
				val = true;
				break;
			} else if (s.startsWith("[") && s.endsWith("]")) {
				break;
			}
		}
		return val;
	}

	/**
	 * ValuePosition is used internally to determine the position of a in the INI file specified in FileName.
	 * <P>
	 * Section is the section in the INI file in which to search for the key.
	 * <P>
	 * key is the name of the key to search for.
	 * <P>
	 * ValuePosition returns an Integer value that indicates the position of the key in the INI file.
	 *
	 * @param Section
	 *            the section name
	 * @param key
	 *            the key name
	 * @return returns the position of the Value. If not found it will return -1
	 */
	private int ValuePosition(String Section, String key) {
		final int start = SectionPosition(Section);
		String s;
		int pos = -1;
		key.length();
		for (int i = start + 1; i < size(); i++) {
			s = get(i).toString();
			if (s.startsWith(key + "=")) {
				pos = i;
				break;
			} else if (s.startsWith("[") && s.endsWith("]")) {
				break;
			}
		}
		return pos;
	}

	/**
	 * Call WriteBool to write a boolean value to an INI file. Section identifies the section in the file that contain
	 * the key to which to write. key is the name of the key for which to set a value. value is the boolean value to
	 * write.
	 * <p>
	 * <b>Note:</b> Attempting to write a data value to a nonexistent section or attempting to write data to a
	 * nonexistent key are not errors. In these cases, WriteBool creates the section and key and sets its initial value
	 * to value.
	 *
	 * @param Section
	 *            the section name
	 * @param key
	 *            the key name
	 * @param value
	 *            the value
	 */
	public void WriteBool(String Section, String key, boolean value) {
		final String s = key + "=" + Boolean.toString(value);
		this.addToList(Section, key, s);
	}

	/**
	 * Implemented comment feature. Must be added after section is created!
	 **/
	public void WriteComment(String Section, String comment) {
		if (this.SectionExist((Section))) {
			add((this.SectionPosition(Section) + 1), "; " + comment);
		}
	}

	/**
	 * Call WriteFloat to write a float value to an INI file. Section identifies the section in the file that contain
	 * the key to which to write. key is the name of the key for which to set a value. value is the float value to
	 * write.
	 * <p>
	 * <b>Note:</b> Attempting to write a data value to a nonexistent section or attempting to write data to a
	 * nonexistent key are not errors. In these cases, WriteFloat creates the section and key and sets its initial value
	 * to value.
	 *
	 * @param Section
	 *            the section name
	 * @param key
	 *            the key name
	 * @param value
	 *            the value
	 */
	public void WriteFloat(String Section, String key, float value) {
		final String s = key + "=" + Float.toString(value);
		this.addToList(Section, key, s);
	}

	/**
	 * Call WriteInteger to write an integer value to an INI file. Section identifies the section in the file that
	 * contain the key to which to write. key is the name of the key for which to set a value. value is the integer
	 * value to write.
	 * <p>
	 * <b>Note:</b> Attempting to write a data value to a nonexistent section or attempting to write data to a
	 * nonexistent key are not errors. In these cases, WriteInteger creates the section and key and sets its initial
	 * value to value.
	 *
	 * @param Section
	 *            the section name
	 * @param key
	 *            the key name
	 * @param value
	 *            the value
	 */
	public void WriteInteger(String Section, String key, int value) {
		final String s = key + "=" + Integer.toString(value);
		this.addToList(Section, key, s);
	}

	/**
	 * Call WriteString to write a string value to an INI file. Section identifies the section in the file that contain
	 * the key to which to write. key is the name of the key for which to set a value. value is the string value to
	 * write.
	 * <p>
	 * <b>Note:</b> Attempting to write a data value to a nonexistent section or attempting to write data to a
	 * nonexistent key are not errors. In these cases, WriteString creates the section and key and sets its initial
	 * value to value.
	 *
	 * @param Section
	 *            the section name
	 * @param key
	 *            the key name
	 * @param value
	 *            the value
	 */
	public void WriteString(String Section, String key, String value) {
		final String s = key + "=" + value;
		this.addToList(Section, key, s);
	}

}
