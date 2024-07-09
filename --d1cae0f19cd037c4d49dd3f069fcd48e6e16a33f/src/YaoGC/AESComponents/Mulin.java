package YaoGC.AESComponents;

import YaoGC.*;

public class Mulin extends CompositeCircuit {
    private static final byte Mulin	= 0;
    public int num;
    public Mulin() {
        super(256, 128, 128, "Mulin");
    }

    public Mulin(int num) {

        super(num*128, 128, (num-1)*128, "Mulin");
        this.num = num;

    }

    public State startExecuting(State state) {
        for (int i = 0; i < 128; i++) {
            for (int j = 0;j<num;j++){
                inputWires[i+128*j].value = state.wires[i+128*j].value;
                inputWires[i+128*j].invd  = state.wires[i+128*j].invd;
                inputWires[i+128*j].setLabel(state.wires[i+128*j].lbl);
                inputWires[i+128*j].setReady();
            }

        }

        return State.fromWires(outputWires);
    }
    protected void createSubCircuits() throws Exception {
        for (int i = 0; i < (num-1)*128; i++)
            subCircuits[i] = new XOR_2_1();
        super.createSubCircuits();
    }

    protected void connectWires(){

            for(int j = num-2;j>=0;j--){
                for (int i = 0; i < 128 ; i++) {
                    if(j==num-2) {
                        inputWires[X(i) + j * 128].connectTo(subCircuits[Y(i) + j * 128].inputWires, 0);
                        inputWires[Y(i) + j * 128].connectTo(subCircuits[Y(i) + j * 128].inputWires, 1);
                    }
                    else{
                        inputWires[Y(i)+j*128].connectTo(subCircuits[Y(i)+ j * 128].inputWires,1);
                        subCircuits[X(i)+j*128].outputWires[0].connectTo(subCircuits[Y(i)+ j * 128].inputWires,0);

                    }

                }
            }


        }



    protected void defineOutputWires() {
        for (int i = 0; i < 128; i++)
            outputWires[i] = subCircuits[i].outputWires[0];
    }

    private static int X(int i) {
        return i + 128;
    }

    private static int Y(int i) {
        return i;
    }

}
