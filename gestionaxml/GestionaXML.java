
package gestionaxml;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.awt.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author Judy
 */
public class GestionaXML {
    String ruta;
    Document documento;
    
    public GestionaXML(String ruta){
        File fichero = new File(ruta);
        abrirXML(fichero);
        this.ruta= ruta;
    }
    
    /**
     * estas 4 lineas abren el fichero
     * @param fich fichero que queremos abrir
     */
    private void abrirXML(File fich){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            documento = db.parse(fich);
            documento.getDocumentElement().normalize();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(GestionaXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @deprecated Este metodo solo crea un solo hijo. Mejor usar el otro.
     * @param nombreNodoPadre
     * @param nombreNodoHijo
     * @param info
     * @return 
     */
    public boolean creaNodos(String nombreNodoPadre,String nombreNodoHijo,String info){ 
        try{
            Node nodoRaiz=documento.getFirstChild();
        
            Element nodoPadre=documento.createElement(nombreNodoPadre);
            Element nodoHijo=documento.createElement(nombreNodoHijo);
            Text informacion = documento.createTextNode(info);
            nodoPadre.appendChild(nodoHijo);
            nodoHijo.appendChild(informacion);
            nodoRaiz.appendChild(nodoPadre);

            return guardaFichero();
        }catch(DOMException e){
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean creaNodos(String nombreNodoPadre,String[] nombreNodosHijos,String[] info){
        try{
            if(nombreNodosHijos.length==info.length){
                Node nodoRaiz=documento.getFirstChild();
                Element nodoPadre=documento.createElement(nombreNodoPadre);

                for (int i = 0; i < nombreNodosHijos.length; i++) {
                    Element nodoHijo=documento.createElement(nombreNodosHijos[i]);
                    Text informacion = documento.createTextNode(info[i]);

                    nodoPadre.appendChild(nodoHijo);
                    nodoHijo.appendChild(informacion);
                    nodoRaiz.appendChild(nodoPadre);
                }
                if(guardaFichero()==true){
                    return true;
                }else{
                    return false;
                }
            }
        }catch(DOMException e){
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean guardaFichero(){
        try {
            //Especifica el formato de salida
            OutputFormat format = new OutputFormat(documento);
            //Especifica que la salida esté indentada.
            format.setIndenting(true);
            //Escribe el contenido en el FILE
            XMLSerializer serializer = new XMLSerializer(new FileOutputStream(ruta),format);
            serializer.serialize(documento);
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GestionaXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GestionaXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int getIndice(String campo,String valor){
        Node raiz = documento.getFirstChild();
        //Declaramos listaHijos y luego los cogemos
        NodeList listaHijos = raiz.getChildNodes();
        //Aqui los recorremos los que se ven y los que no se ven
        for(int i = 0;i<listaHijos.getLength();i++){
            //Coges el nodoHijo de la posicion "i"
            Node nodoTemporal = listaHijos.item(i);
            
            //Este if comprueba que los nodos son los visibles            
            if(nodoTemporal.getNodeType()==Node.ELEMENT_NODE){
                //Coges los hijos de nodoTemporal
                NodeList hijosTemporal = nodoTemporal.getChildNodes();
                
                for (int j = 0; j < hijosTemporal.getLength(); j++) {
                    //Coges el nodoHijo de la posicion "j"
                    Node hijoTem = hijosTemporal.item(j);
                    
                    //Este if comprueba que los nodos son los visibles
                    if(hijoTem.getNodeType()==Node.ELEMENT_NODE) {
                        if(hijoTem.getNodeName().equals(campo)){
                            if(hijoTem.getTextContent().equals(valor)){
                                return i;
                            }
                        }
                    }
                }
            }
        }
        return -404;
    }
    
    public String[] getInfoNodo(int indice){
        List listaInfo = new List();
        
        NodeList listaHijosNodosRaiz = documento.getFirstChild().getChildNodes();//Lista usuarios(todos)
        
        Node nodo = listaHijosNodosRaiz.item(indice);//Usuario que me piden
        NodeList hijosNodo= nodo.getChildNodes();//Lista hijos del nodo seleccionado
        for (int i = 0; i < hijosNodo.getLength(); i++) {//Cojo la longitud de hijosNodo
            Node hijoNodo = hijosNodo.item(i);//El nodo en esa posicion [i]
            if(hijoNodo.getNodeType()==Node.ELEMENT_NODE){
                String infoNodoSelec = hijoNodo.getTextContent();
                listaInfo.add(infoNodoSelec);
            }
            
        }
        
        return listaInfo.getItems();
     }

    public String[] getInfoTodosLosNodos(){
        List lista = new List();
        //Nodo raiz
        Node raiz = documento.getFirstChild();
        //Declaramos listaHijos y luego los cogemos
        NodeList listaHijos = raiz.getChildNodes();
        //Aqui los recorremos los que se ven y los que no se ven
        for(int i = 0;i<listaHijos.getLength();i++){
            Node nodoTemporal = listaHijos.item(i);
            if(nodoTemporal.getNodeType()==Node.ELEMENT_NODE){
                String[] infoNodo = getInfoNodo(i);
                String cadenaInfo = "";                        
                for (int j = 0; j < infoNodo.length; j++) {
                    if(j<infoNodo.length-1){
                        cadenaInfo+=infoNodo[j]+" - ";
                    }
                    else{
                        cadenaInfo+=infoNodo[j];
                    }
                }
                lista.add(cadenaInfo);              
            }
        }
        return lista.getItems();
    }
    
    public boolean modificaNodo(int indice,String nombreHijo,String nuevaInfo){
        
        NodeList listaHijosNodosRaiz = documento.getFirstChild().getChildNodes();//Lista usuarios(todos)
        Node nodo = listaHijosNodosRaiz.item(indice);//Usuario que me piden
        NodeList hijosNodo= nodo.getChildNodes();//Lista hijos del nodo seleccionado
        for (int i = 0; i < hijosNodo.getLength(); i++) {//Cojo la longitud de los hijosNodos
            Node hijoNodo = hijosNodo.item(i);//El nodo en la posicion [i]; 
            if(hijoNodo.getNodeType()==Node.ELEMENT_NODE){
                String nomHijo = hijoNodo.getNodeName();
                if(nomHijo.equals(nombreHijo)){
                    Node nodoTexto = hijoNodo.getFirstChild();
                    nodoTexto.setNodeValue(nuevaInfo);
                    guardaFichero();
                    break;
                }
            }
        }
        
        return true;
        
    }
    
    public boolean borraInfo(int indice){
        Node nodoRaiz = documento.getFirstChild();
        NodeList listaHijosNodosRaiz = nodoRaiz.getChildNodes();//Lista usuarios(todos)
        Node nodo = listaHijosNodosRaiz.item(indice);//Usuario que me piden
        nodoRaiz.removeChild(nodo);
        guardaFichero();
        
        
    
    return true;
    }
}
