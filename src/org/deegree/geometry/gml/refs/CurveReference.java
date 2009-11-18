//$HeadURL: svn+ssh://mschneider@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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

package org.deegree.geometry.gml.refs;

import java.util.List;

import org.deegree.commons.gml.GMLObjectResolver;
import org.deegree.commons.uom.Measure;
import org.deegree.commons.uom.Unit;
import org.deegree.commons.utils.Pair;
import org.deegree.geometry.points.Points;
import org.deegree.geometry.primitive.Curve;
import org.deegree.geometry.primitive.LineString;
import org.deegree.geometry.primitive.Point;
import org.deegree.geometry.primitive.segments.CurveSegment;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @param <T>
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: schneider $
 * 
 * @version $Revision: $, $Date: $
 */
public class CurveReference<T extends Curve> extends GeometricPrimitiveReference<T> implements Curve {

    /**
     * Creates a new {@link CurveReference} instance.
     * 
     * @param resolver
     *            used for resolving the reference, must not be <code>null</code>
     * @param uri
     *            the geometry's uri, must not be <code>null</code>
     * @param baseURL
     *            base URL for resolving the uri, may be <code>null</code> (no resolving of relative URLs)
     */
    public CurveReference( GMLObjectResolver resolver, String uri, String baseURL ) {
        super( resolver, uri, baseURL );
    }

    @Override
    public LineString getAsLineString() {
        return getReferencedGeometry().getAsLineString();
    }

    @Override
    public Pair<Point, Point> getBoundary() {
        return getReferencedGeometry().getBoundary();
    }

    @Override
    public Points getControlPoints() {
        return getReferencedGeometry().getControlPoints();
    }

    @Override
    public List<CurveSegment> getCurveSegments() {
        return getReferencedGeometry().getCurveSegments();
    }

    @Override
    public CurveType getCurveType() {
        return getReferencedGeometry().getCurveType();
    }

    @Override
    public Point getEndPoint() {
        return getReferencedGeometry().getEndPoint();
    }

    @Override
    public Measure getLength( Unit requestedUnit ) {
        return getReferencedGeometry().getLength( requestedUnit );
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return getReferencedGeometry().getPrimitiveType();
    }

    @Override
    public Point getStartPoint() {
        return getReferencedGeometry().getStartPoint();
    }

    @Override
    public boolean isClosed() {
        return getReferencedGeometry().isClosed();
    }
}
