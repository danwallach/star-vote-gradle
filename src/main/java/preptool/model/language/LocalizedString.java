/**
  * This file is part of VoteBox.
  * 
  * VoteBox is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License version 3 as published by
  * the Free Software Foundation.
  * 
  * You should have received a copy of the GNU General Public License
  * along with VoteBox, found in the root of any distribution or
  * repository containing all or part of VoteBox.
  * 
  * THIS SOFTWARE IS PROVIDED BY WILLIAM MARSH RICE UNIVERSITY, HOUSTON,
  * TX AND IS PROVIDED 'AS IS' AND WITHOUT ANY EXPRESS, IMPLIED OR
  * STATUTORY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, WARRANTIES OF
  * ACCURACY, COMPLETENESS, AND NONINFRINGEMENT.  THE SOFTWARE USER SHALL
  * INDEMNIFY, DEFEND AND HOLD HARMLESS RICE UNIVERSITY AND ITS FACULTY,
  * STAFF AND STUDENTS FROM ANY AND ALL CLAIMS, ACTIONS, DAMAGES, LOSSES,
  * LIABILITIES, COSTS AND EXPENSES, INCLUDING ATTORNEYS' FEES AND COURT
  * COSTS, DIRECTLY OR INDIRECTLY ARISING OUR OF OR IN CONNECTION WITH
  * ACCESS OR USE OF THE SOFTWARE.
 */

package preptool.model.language;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;

/**
 * A class that contains multiple translations of a String. It is a wrapper for
 * a map from language to string.
 *
 * @author Corey Shaw
 */
public class LocalizedString {

	/**
	 * The map containing the strings
	 */
	private HashMap<Language, String> map;

	/**
	 * Constructs a LocalizedString with no translations
	 */
	public LocalizedString() {
		map = new HashMap<>();
	}

	/**
	 * Checks all Strings in both maps, and makes sure there are the
	 * same number of localizations
     *
     * @return true if the input map is the same size, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LocalizedString))
			return false;
		LocalizedString rhs = (LocalizedString)obj;
		if (map.size() != rhs.map.size())
			return false;
		for (Language lang: map.keySet())
			if (!map.get(lang).equals(rhs.map.get(lang)))
				return false;
		return true;
	}
	
	/**
	 * Returns the translation for the given language
     *
	 * @param lang the language to get the translation for
	 * @return the translation into the specified language,
     *         or an empty string if we don't have a translation
	 */
	public String get(Language lang) {
		String val = map.get(lang);
		if (val == null)
			return "";
		else
			return val;
	}

	/**
	 * Sets (or adds) the translation for the given language to the given String
     *
	 * @param lang the language
	 * @param val the String
	 */
	public void set(Language lang, String val) {
		map.put(lang, val);
	}

	/**
	 * Converts this card to a savable XML representation, to be opened later
     *
	 * @param doc the document this component is a part of@param name name to attach to the element
	 * @return          the XML element representation for this card
	 */
	public Element toSaveXML(Document doc, String name) {
		Element elt = doc.createElement("LocalizedString");
		elt.setAttribute("name", name);

        /* For each language, write it into the XML */
		for (Language lang: map.keySet()) {
			Element stringElt = doc.createElement("String");
            stringElt.setAttribute("language", lang.getName());
			stringElt.setAttribute("text", map.get(lang));
			elt.appendChild(stringElt);
		}
		return elt;
	}

	/**
	 * Parses XML into a LocalizedString object
     *
	 * @param elt the element
	 * @return the LocalizedString
	 */
	public static LocalizedString parseXML(Element elt) {
        /* Sanity check */
		assert elt.getTagName().equals("LocalizedString");


		LocalizedString ls = new LocalizedString();

		NodeList list = elt.getElementsByTagName("String");
		for (int i = 0; i < list.getLength(); i++) {
			Element child = (Element) list.item(i);
			ls.set(Language.getLanguageForName(child.getAttribute("language")),
					child.getAttribute("text"));
		}
		return ls;
	}

}
