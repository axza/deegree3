//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
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
package org.deegree.client.mdeditor.mapping;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.deegree.client.mdeditor.model.DataGroup;
import org.deegree.client.mdeditor.model.FormField;
import org.deegree.client.mdeditor.model.mapping.MappingElement;
import org.deegree.client.mdeditor.model.mapping.MappingGroup;
import org.deegree.client.mdeditor.model.mapping.MappingInformation;
import org.jaxen.expr.NameStep;
import org.jaxen.saxpath.Axis;
import org.slf4j.Logger;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class MappingExporter {

    private static final Logger LOG = getLogger( MappingExporter.class );

    public static void export( File file, MappingInformation mapping, Map<String, FormField> formFields,
                               Map<String, List<DataGroup>> dataGroups )
                            throws XMLStreamException, FileNotFoundException, FactoryConfigurationError {
        LOG.debug( "Export dataset in file " + file.getAbsolutePath() + " selected mapping is " + mapping.toString() );
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter( new FileOutputStream( file ) );
        writer.writeStartDocument();

        List<MappingElement> mappingElements = mapping.getMappingElements();
        Iterator<MappingElement> it = mappingElements.iterator();
        if ( it.hasNext() ) {
            MappingElement currentElement = it.next();
            int currentIndex = 0;
            while ( currentElement != null ) {
                LOG.debug( "handle mapping element " + currentElement );
                MappingElement nextElement = null;
                if ( it.hasNext() ) {
                    nextElement = it.next();
                }
                String ffPath = currentElement.getFormFieldPath();
                List<NameStep> nextSteps = new ArrayList<NameStep>();
                if ( nextElement != null ) {
                    nextSteps = nextElement.getSchemaPathAsSteps( mapping.getNsContext() );
                }

                if ( currentElement instanceof MappingGroup ) {
                    currentIndex = writeMappingGroup( writer, nextSteps, currentIndex, (MappingGroup) currentElement,
                                                      dataGroups.get( ffPath ), ffPath, mapping );
                } else {
                    List<NameStep> currentSteps = currentElement.getSchemaPathAsSteps( mapping.getNsContext() );
                    if ( formFields.containsKey( ffPath ) && formFields.get( ffPath ) != null ) {
                        currentIndex = writeMappingElement( writer, currentSteps, nextSteps, currentIndex,
                                                            formFields.get( ffPath ).getValue(), ffPath, mapping );
                    }
                }
                currentElement = nextElement;
            }
        }

        writer.writeEndDocument();
        writer.close();
    }

    private static void wtiteDataGroup( XMLStreamWriter writer, MappingInformation mapping,
                                        List<MappingElement> mappingElements, Map<String, Object> values )
                            throws XMLStreamException {
        Iterator<MappingElement> it = mappingElements.iterator();
        if ( it.hasNext() ) {
            MappingElement currentElement = it.next();
            int currentIndex = 0;
            while ( currentElement != null ) {
                MappingElement nextElement = null;
                if ( it.hasNext() ) {
                    nextElement = it.next();
                }
                String ffPath = currentElement.getFormFieldPath();
                if ( values.containsKey( ffPath ) && values.get( ffPath ) != null ) {
                    List<NameStep> nextSteps = new ArrayList<NameStep>();
                    if ( nextElement != null ) {
                        nextSteps = nextElement.getSchemaPathAsSteps( mapping.getNsContext() );
                    }
                    List<NameStep> currentSteps = currentElement.getSchemaPathAsSteps( mapping.getNsContext() );

                    currentIndex = writeMappingElement( writer, currentSteps, nextSteps, currentIndex,
                                                        values.get( ffPath ), ffPath, mapping );

                }
                currentElement = nextElement;
            }
        }
    }

    private static int writeMappingGroup( XMLStreamWriter writer, List<NameStep> nextSteps, int currentIndex,
                                          MappingGroup group, List<DataGroup> dataGroups, String ffPath,
                                          MappingInformation mapping )
                            throws XMLStreamException {
        if ( dataGroups != null ) {
            LOG.debug( "write mapping group" );
            List<NameStep> groupSteps = group.getSchemaPathAsSteps( mapping.getNsContext() );
            for ( ; currentIndex < groupSteps.size(); currentIndex++ ) {
                NameStep qName = groupSteps.get( currentIndex );
                String prefix = qName.getPrefix();
                writer.writeStartElement( prefix, qName.getLocalName(), mapping.getNsContext().getURI( prefix ) );
            }
            for ( DataGroup dg : dataGroups ) {
                Map<String, Object> values = dg.getValues();
                wtiteDataGroup( writer, mapping, group.getMappingElements(), values );
            }
            for ( int i = 0; i < groupSteps.size() - currentIndex; i++ ) {
                writer.writeEndElement();
            }

        }
        return currentIndex;
    }

    private static int writeMappingElement( XMLStreamWriter writer, List<NameStep> currentSteps,
                                            List<NameStep> nextSteps, int currentIndex, Object value, String ffPath,
                                            MappingInformation mapping )
                            throws XMLStreamException {
        if ( value != null ) {
            for ( ; currentIndex < currentSteps.size(); currentIndex++ ) {
                NameStep nameStep = currentSteps.get( currentIndex );
                // found list of elements
                if ( "*".equals( nameStep.getLocalName() ) ) {
                    LOG.debug( "found mapping to list of single element" );
                    writeValue( writer, currentSteps.subList( currentIndex + 1, currentSteps.size() ), value, mapping );
                    break;
                } else if ( Axis.ATTRIBUTE == nameStep.getAxis() ) {
                    writeAttribute( writer, nameStep, value, mapping );
                    break;
                } else {
                    String prefix = nameStep.getPrefix();
                    writer.writeStartElement( prefix, nameStep.getLocalName(), mapping.getNsContext().getURI( prefix ) );
                    if ( currentIndex == currentSteps.size() - 1 ) {
                        writer.writeCharacters( value.toString() );
                    }
                }
            }
        }
        return finishStepsUntilNextCommon( writer, currentSteps, nextSteps, currentIndex );
    }

    private static void writeAttribute( XMLStreamWriter writer, NameStep nameStep, Object value,
                                        MappingInformation mapping )
                            throws XMLStreamException {
        LOG.debug( "write attribute " + nameStep + ", value is " + value );
        String prefix = nameStep.getPrefix();
        String ns = mapping.getNsContext().getURI( prefix );
        if ( ns != null ) {
            writer.writeAttribute( prefix, ns, nameStep.getLocalName(), value.toString() );
        } else {
            writer.writeAttribute( nameStep.getLocalName(), value.toString() );
        }
    }

    private static void writeValue( XMLStreamWriter writer, List<NameStep> currentSteps, Object value,
                                    MappingInformation mapping )
                            throws XMLStreamException {
        if ( value instanceof List<?> ) {
            LOG.debug( "write list of values" );
            for ( Object o : (List<?>) value ) {
                writeSteps( writer, currentSteps, o.toString(), mapping );
            }
        } else {
            writeSteps( writer, currentSteps, value.toString(), mapping );
        }
    }

    private static void writeSteps( XMLStreamWriter writer, List<NameStep> currentSteps, String value,
                                    MappingInformation mapping )
                            throws XMLStreamException {
        for ( NameStep step : currentSteps ) {
            String prefix = step.getPrefix();
            writer.writeStartElement( prefix, step.getLocalName(), mapping.getNsContext().getURI( prefix ) );
        }
        writer.writeCharacters( value );
        for ( int i = 0; i < currentSteps.size(); i++ ) {
            writer.writeEndElement();
        }
    }

    private static int finishStepsUntilNextCommon( XMLStreamWriter writer, List<NameStep> currentSteps,
                                                   List<NameStep> nextSteps, int currentIndex )
                            throws XMLStreamException {
        int equalSteps = 0;
        for ( int i = 0; i < currentSteps.size() && i < nextSteps.size(); i++ ) {
            if ( isSame( currentSteps.get( i ), nextSteps.get( i ) ) ) {
                equalSteps++;
            } else {
                break;
            }
        }
        int stepsToClose = currentSteps.size() - equalSteps - ( currentSteps.size() - currentIndex );
        for ( int i = 0; i < stepsToClose; i++ ) {
            writer.writeEndElement();
        }

        return equalSteps;
    }

    private static boolean isSame( NameStep one, NameStep two ) {
        if ( one != null && two != null ) {
            if ( one.getAxis() == two.getAxis()
                 && one.getLocalName().equals( two.getLocalName() )
                 && ( ( one.getPrefix() == null && two.getPrefix() == null ) || ( one.getPrefix() != null && one.getPrefix().equals(
                                                                                                                                     two.getPrefix() ) ) ) ) {
                return true;
            }
        }
        return false;
    }
}
