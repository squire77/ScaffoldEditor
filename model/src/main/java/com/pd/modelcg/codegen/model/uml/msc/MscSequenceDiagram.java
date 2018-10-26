package com.pd.modelcg.codegen.model.uml.msc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MscSequenceDiagram implements Serializable {
    public MscSequenceDiagram( String name ) {
	this.name = name;
    }

    public  String              name;
    public  List<MscObject>     participants = new ArrayList<MscObject>();
}


