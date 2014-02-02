package model;

public enum NodeType {
	ARRIVAL(1), 
	TRANSIT(2), 
	DEPARTURE(3);
     
     private final int value;

     private NodeType(int value) {
    	 this.value = value;
     }
     
     public int Value() {
         return value;
     }
     
     public static NodeType fromInteger(int x) {
         switch(x) {
         case 1:
             return ARRIVAL;
         case 2:
             return TRANSIT;
         case 3:
             return DEPARTURE;
         }
         return null;
     }
}; 

