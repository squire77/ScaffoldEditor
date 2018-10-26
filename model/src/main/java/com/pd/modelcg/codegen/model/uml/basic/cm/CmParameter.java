package com.pd.modelcg.codegen.model.uml.basic.cm;

import com.pd.modelcg.codegen.model.uml.basic.UmlTypedElement;
import com.pd.modelcg.codegen.model.uml.datatypes.AslInteger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Observable;
import java.util.Observer;

@XmlType(propOrder = { "direction", "multiplicity" })
public class CmParameter extends UmlTypedElement implements Observer {
    public static String directionTypeToString(int dt) {
        switch (dt){
            case 0: return "IN";
            case 1: return "OUT";
            case 2: return "IN_OUT";
            default: return "UNKNOWN VISIBLITY KIND (" + dt + ")";
        }
    }
    
    public static int valueOfDirectionType(String name) {
        if (name.equalsIgnoreCase("IN")) return 0;
        if (name.equalsIgnoreCase("OUT")) return 1;
        if (name.equalsIgnoreCase("IN_OUT")) return 2;
        return -1;
    }
    public static boolean isValidDirectionType(String name) {
        return (name != null && 
                (name.equalsIgnoreCase("IN") ||
                 name.equalsIgnoreCase("OUT") ||
                 name.equalsIgnoreCase("IN_OUT")));
    }
    
    public CmParameter() {
    }
    
    public CmParameter(String name) {
        super(name);  
        
        //Parameters are private by default
        setVisibilityKind("PRIVATE");   
        
        //Parameters have type int by default
        setTypeID(AslInteger.getInstance().getID());
        
        this.direction = 0; //IN        
    }   

    public void update(Observable o, Object obj) {
        setChanged();
        notifyObservers(obj);
    }
    
    public int getDirection() {
        return this.direction;
    }
    public void setDirection(int direction) {
        this.direction = direction;
        
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
            
    private int                     direction;    
    
    @XmlElement(name = "multiplicity")
    private CmMultiplicityElement   multiplicity;     
}
