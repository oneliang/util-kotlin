package org.kabeja.dxf;

import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGGenerator;
import org.kabeja.xml.SAXGenerator;
import org.kabeja.xml.SAXPrettyOutputter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class Test {
    public static void main(String[] args) throws Exception {
        Parser parser = ParserBuilder.createDefaultParser();
        String fullFilename = "C:\\Users\\Administrator\\Desktop\\test.dxf";
        parser.parse(new FileInputStream(fullFilename), DXFParser.DEFAULT_ENCODING);
        DXFDocument doc = parser.getDocument();
        boolean outputDTD = false;
        String outputFullFilename = "C:\\Users\\Administrator\\Desktop\\test.svg";
        OutputStream out = new FileOutputStream(outputFullFilename);
        SAXPrettyOutputter writer = new SAXPrettyOutputter(out, SAXPrettyOutputter.DEFAULT_ENCODING);
        if (outputDTD) {
            writer.setDTD(SVGConstants.SVG_DTD_1_0);
        }
        SAXGenerator svgGenerator = new SVGGenerator();
        svgGenerator.setProperties(new HashMap<String, String>());
        svgGenerator.generate(doc, writer, new HashMap<String, String>());
    }
}
