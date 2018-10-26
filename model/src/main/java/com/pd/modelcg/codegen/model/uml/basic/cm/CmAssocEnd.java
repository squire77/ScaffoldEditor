package com.pd.modelcg.codegen.model.uml.basic.cm;

import com.pd.modelcg.codegen.model.uml.basic.UmlNamedElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Observable;
import java.util.Observer;

@XmlType(propOrder = { "assocID", "classID", "static", "readOnly", "navigable", "isComposite", "aggregate", "multiplicity"})
public class CmAssocEnd extends UmlNamedElement implements Observer {
    public CmAssocEnd() {
    }
       
    public CmAssocEnd(CmAssociation assoc, boolean isAggregate) {
        this.assocID = assoc.getID(); //do not store assoc itself to ease externalization  
        this.isStatic = false;
        this.isReadOnly = false;
        this.isNavigable = false;
        this.isComposite = false;
        this.isAggregate = isAggregate;   
    }

    public void update(Observable o, Object obj) {
        //multiplicity changed so tell our observers
        setChanged();
        notifyObservers(obj);      
    }
    
    public CmAssociation getAssociation() {
        return CmAssociation.getAssociation(this.assocID);
    }
            
    public CmClass getClazz() {
        return CmClass.getClass(this.classID);
    }
        
    public String getClassID() {
        return this.classID;
    }
    void setClassID(String classID) {
        this.classID = classID; 
    }

    
    public boolean isStatic() {
        return this.isStatic;
    }
    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;

        setChanged();
        notifyObservers();
    }
    
    public boolean isReadOnly() {
        return this.isReadOnly;
    }
    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
        
        setChanged();
        notifyObservers();        
    }  
    
    public boolean isNavigable() {
        return this.isNavigable;
    }
    public void setNavigable(boolean isNavigable) {
        this.isNavigable = isNavigable;
        
        setChanged();
        notifyObservers();
    }
    
    public boolean isComposite() {
        return isComposite;
    }
    //called only by composite who will then notify observers
    void setComposite(boolean isComposite) {
        this.isComposite = isComposite;           
    }   
    
    public boolean isAggregate() {
        return isAggregate;
    }
    public void setAggregate(boolean isAggregate) {
        this.isAggregate = isAggregate;           
        
        setChanged();
        notifyObservers();
    }     
    
    public CmMultiplicityElement getMultiplicity() {
        if (this.multiplicity == null) {
            this.multiplicity = new CmMultiplicityElement();
            this.multiplicity.addObserver(this);
        }
        
        return this.multiplicity;
    }
    
    @XmlElement(name = "assocID")
    private String                  assocID;
    
    @XmlElement(name = "classID")
    private String                  classID; 
                
    private boolean                 isStatic;     
    private boolean                 isReadOnly; 
    private boolean                 isNavigable;

    @XmlElement(name = "isComposite")
    private boolean                 isComposite;

    private boolean                 isAggregate;
           
    @XmlElement(name = "multiplicity")
    private CmMultiplicityElement   multiplicity;
}
