package connections;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;



public class XMLParser {

	private static final String ns = null;

	// We don't use namespaces

	public static class Key {
		public String value;
		public String keyName;
		public List<Key> keysList;

		private Key(String value, String keyName) {
			this.value = value;
			this.keyName = keyName;
		}

		private Key(List<Key> keysList) {
			this.keysList = keysList;

		}
	}

	public List<Key> parse(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readResponse(parser);
		} finally {
			in.close();
		}
	}

	// este vais ser o read response que irá baixar para o nivel do multiple
	private List<Key> readResponse(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<Key> entries = new ArrayList<Key>();

		parser.require(XmlPullParser.START_TAG, ns, "RESPONSE");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("MULTIPLE")) {

				List<Key> keys = readMultiple(parser);

				entries.addAll(keys);
			} else if (name.equals("SINGLE")) {

				List<Key> keys = readSingle(parser);

				entries.addAll(keys);

			} else {
				skip(parser);
			}
		}
		Log.d("parser", "1readResponse entries isEmpty?->"
				+ (entries.isEmpty() ? "true" : "false"));
		return entries;
	}

	// este vais ser o read multiple que irá baixar para o nivel do single
	private List<Key> readMultiple(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<Key> entries = new ArrayList<Key>();

		parser.require(XmlPullParser.START_TAG, ns, "MULTIPLE");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

			// Starts by looking for the entry tag

			if (name.equals("SINGLE")) {

				List<Key> keys = readSingle(parser);

				entries.addAll(keys);

			}
		}

		Log.d("parser", "2readMultiple entries isEmpty?->"
				+ (entries.isEmpty() ? "true" : "false"));
		return entries;
	}

	// este vais ser o read single que irá guardar todas as keys
	private List<Key> readSingle(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<Key> entries = new ArrayList<Key>();

		String keyName = "";
		parser.require(XmlPullParser.START_TAG, ns, "SINGLE");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("KEY")) {

				for (int i = 0; i < parser.getAttributeCount(); i++) {
					if (parser.getAttributeName(i).equals("name")) {
						keyName = parser.getAttributeValue(i);
						break;
					}
				}

				Key k = readKey(parser);
				if (k != null) {
					k.keyName = keyName;
					entries.add(k);

				}

			} else {
				skip(parser);
			}
		}

		Log.d("parser", "3readSingle entries isEmpty?->"
				+ (entries.isEmpty() ? "true" : "false"));
		Log.d("parser", "Keyname?->" + keyName);
		return entries;
	}

	private Key readKey(XmlPullParser parser) throws XmlPullParserException,
			IOException {

		parser.require(XmlPullParser.START_TAG, ns, "KEY");
		String value = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String nodeName = parser.getName();
			if (nodeName.equals("VALUE")) {

				// atributeName = parser.getAttributeValue(0);
				value = readValue(parser);
				// this nextTag() it's required to "SINGLE" loop for all the
				// "KEYS"
				parser.nextTag();
				return new Key(value, "");

			} else if (nodeName.equals("MULTIPLE")) {
				return new Key(readMultiple(parser));

			} else {
				skip(parser);
			}

		}
		return null;
	}

	private String readValue(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "VALUE");
		String value = "";
		if (parser.next() == XmlPullParser.TEXT) {
			value = parser.getText();
			parser.nextTag();
		}

		parser.require(XmlPullParser.END_TAG, ns, "VALUE");
		return value;
	}

	// Skips tags the parser isn't interested in. Uses depth to handle nested
	// tags. i.e.,
	// if the next tag after a START_TAG isn't a matching END_TAG, it keeps
	// going until it
	// finds the matching END_TAG (as indicated by the value of "depth" being
	// 0).
	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}

		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}

	}

}
