package com.pd.modelcg.codegen.model.uml.datatypes;

import com.pd.modelcg.codegen.model.uml.basic.UmlPrimitiveType;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "test.modeling.uml.datatype")
public class AslVoid extends UmlPrimitiveType {
    private AslVoid() {
        super("void");
    }

    @Override
    public boolean isVoid() { return true; }

    public static AslVoid getInstance() {
        if (_instance == null) {
            _instance = new AslVoid();
        }
        
        return _instance;
    }

    private static AslVoid _instance;
}
