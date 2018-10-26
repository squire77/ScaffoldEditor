package com.pd.modelcg.codegen.model.uml.datatypes;

import com.pd.modelcg.codegen.model.uml.basic.UmlPrimitiveType;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "test.modeling.uml.datatype")
public class AslUnlimitedNatural extends UmlPrimitiveType {
    private AslUnlimitedNatural() {
        super("uint");
    }

    @Override
    public boolean isUnlimitedNatural() { return true; }
        
    public static AslUnlimitedNatural getInstance() {
        if (_instance == null) {
            _instance = new AslUnlimitedNatural();
        }
        
        return _instance;
    }

    private static AslUnlimitedNatural _instance;
}
