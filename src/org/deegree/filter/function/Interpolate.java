//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.filter.function;

import static java.awt.Color.decode;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.toHexString;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.deegree.commons.utils.math.MathUtils.round;
import static org.deegree.rendering.r2d.se.parser.SymbologyParser.updateOrContinue;
import static org.deegree.rendering.r2d.se.unevaluated.Continuation.SBUPDATER;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.deegree.filter.MatchableObject;
import org.deegree.filter.expression.Function;
import org.deegree.rendering.r2d.se.unevaluated.Continuation;

/**
 * <code>Interpolate</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class Interpolate extends Function {

    private StringBuffer value;

    private Continuation<StringBuffer> contn;

    private LinkedList<Double> datas = new LinkedList<Double>();

    private LinkedList<StringBuffer> values = new LinkedList<StringBuffer>();

    private LinkedList<Continuation<StringBuffer>> valueContns = new LinkedList<Continuation<StringBuffer>>();

    private boolean color = false;

    private boolean linear = true, cosine, cubic;

    /***/
    public Interpolate() {
        super( "Interpolate", null );
    }

    private static final Color interpolateColor( final Color fst, final Color snd, final double f ) {
        final double f1m = 1 - f;
        int red = (int) ( fst.getRed() * f1m + snd.getRed() * f );
        int green = (int) ( fst.getGreen() * f1m + snd.getGreen() * f );
        int blue = (int) ( fst.getBlue() * f1m + snd.getBlue() * f );
        int alpha = (int) ( fst.getAlpha() * f1m + snd.getAlpha() * f );
        return new Color( red, green, blue, alpha );
    }

    private static final double interpolate( final double fst, final double snd, final double f ) {
        return fst * ( 1 - f ) + snd * f;
    }

    @Override
    public Object[] evaluate( MatchableObject f ) {
        StringBuffer sb = new StringBuffer( value.toString().trim() );
        if ( contn != null ) {
            contn.evaluate( sb, f );
        }

        double val = parseDouble( sb.toString() );

        Iterator<Double> data = datas.iterator();
        Iterator<StringBuffer> vals = values.iterator();
        Iterator<Continuation<StringBuffer>> contns = valueContns.iterator();

        double cur = data.next();
        StringBuffer intVal = vals.next();
        Continuation<StringBuffer> contn = contns.next();

        while ( val > cur && data.hasNext() ) {
            cur = data.next();
            intVal = vals.next();
            contn = contns.next();
        }

        StringBuffer buf = new StringBuffer( intVal.toString().trim() );
        if ( contn != null ) {
            contn.evaluate( sb, f );
        }
        String fstString = buf.toString();

        if ( !data.hasNext() ) {
            return new Object[] { fstString };
        }

        buf = new StringBuffer( vals.next().toString().trim() );
        contn = contns.next();
        if ( contn != null ) {
            contn.evaluate( sb, f );
        }
        String sndString = buf.toString();

        double next = data.next();
        double fac = ( val - cur ) / ( next - cur );

        if ( color ) {
            Color fst = decode( sndString );
            Color snd = decode( sndString );
            Color res = interpolateColor( fst, snd, fac );
            return new Object[] { "#" + toHexString( res.getRGB() ) };
        }

        return new Object[] { "" + interpolate( parseDouble( fstString ), parseDouble( sndString ), fac ) };
    }

    /**
     * @param in
     * @throws XMLStreamException
     */
    public void parse( XMLStreamReader in )
                            throws XMLStreamException {
        in.require( START_ELEMENT, null, "Interpolate" );

        String mode = in.getAttributeValue( null, "mode" );
        if ( mode != null ) {
            linear = mode.equals( "linear" );
            cosine = mode.equals( "cosine" );
            cubic = mode.equals( "cubic" );
        }

        String method = in.getAttributeValue( null, "method" );
        if ( method != null ) {
            color = method.equals( "color" );
        }

        while ( !( in.isEndElement() && in.getLocalName().equals( "Interpolate" ) ) ) {
            in.nextTag();

            if ( in.getLocalName().equals( "LookupValue" ) ) {
                value = new StringBuffer();
                contn = updateOrContinue( in, "LookupValue", value, SBUPDATER, null );
            }

            if ( in.getLocalName().equals( "InterpolationPoint" ) ) {
                while ( !( in.isEndElement() && in.getLocalName().equals( "InterpolationPoint" ) ) ) {
                    in.nextTag();

                    if ( in.getLocalName().equals( "Data" ) ) {
                        datas.add( Double.valueOf( in.getElementText() ) );
                    }

                    if ( in.getLocalName().equals( "Value" ) ) {
                        StringBuffer sb = new StringBuffer();
                        valueContns.add( updateOrContinue( in, "Value", sb, SBUPDATER, null ) );
                        values.add( sb );
                    }
                }
            }

        }

        in.require( END_ELEMENT, null, "Interpolate" );
    }

    /**
     * @param in
     * @throws XMLStreamException
     */
    public void parseSLD100( XMLStreamReader in )
                            throws XMLStreamException {
        color = true;

        while ( !( in.isEndElement() && in.getLocalName().equals( "ColorMap" ) ) ) {
            in.nextTag();

            if ( in.getLocalName().equals( "ColorMapEntry" ) ) {
                String color = in.getAttributeValue( null, "color" );
                String opacity = in.getAttributeValue( null, "opacity" );
                String quantity = in.getAttributeValue( null, "quantity" );
                datas.add( quantity != null ? Double.valueOf( quantity ) : 0 );
                if ( opacity != null ) {
                    color = "#" + toHexString( round( parseDouble( opacity ) * 255 ) ) + color.substring( 1 );
                }
                values.add( new StringBuffer( color ) );
                // for legend generation, later on?
                // String label= in.getAttributeValue(null, "label" );
                in.nextTag();
            }
        }
    }

}
