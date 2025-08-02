package mineplex.minecraft.game.classcombat.Class.repository.token;

import java.io.PrintStream;

public class SlotToken {
  public String Material;
  public int Amount;
  
  public void printInfo() {
    System.out.println("Material : " + this.Material);
    System.out.println("Amount : " + this.Amount);
  }
}
