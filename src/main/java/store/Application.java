package store;

import camp.nextstep.edu.missionutils.Console;
import java.util.*;
import store.Model.OrderItem;
import store.Model.Products;
import store.Model.Promotions;
import store.Controller.Controller;
import store.View.OutputView;

public class Application {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.run();
    }
}
