import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.*



suspend fun order(ID: Int, n_burger:Int, n_fries:Int, n_drinks:Int) = coroutineScope{
    if (n_burger != 0) {
        launch {
            burger(n_burger)
            //println("Burgers for order ${ID} are ready")
        }
    }
    if (n_fries != 0) {
        launch {
            fries(n_fries)
            //println("Fries for order ${ID} are ready")
        }
    }
    if (n_drinks != 0) {
        launch {
            coldDrink(n_drinks)
            //println("Drinks for order ${ID} are ready")
        }
    }

}

suspend fun burger(n:Int){
    val t = 5000L * n
    delay(t)
    //A burger is made
}

suspend fun coldDrink(n:Int){
    val t = 1000L * n
    delay(t)
    //A cold drink is made
}
suspend fun fries(n:Int){
    val t = 2500L * n
    delay(t)
    //Fries are made
}



@Composable
fun DropMenu(options:Array<String> = arrayOf("0", "1", "2", "3", "4", "5")) : Int{
    var expanded = remember { mutableStateOf(false) }
    var selectedOption = remember { mutableStateOf("0") }
    //val options = listOf("Option 1", "Option 2", "Option 3")

    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        //Text(text = "Selected option: ${selectedOption.value}")

        DropdownMenu(
            modifier = Modifier.width(200.dp),
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    selectedOption.value = option
                    expanded.value = false
                }) {
                    Text(text = "   "+option+"    ")
                }
            }
        }

        Button(
            onClick = { expanded.value = true },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(if (selectedOption.value != "0") "${selectedOption.value}" else "-SELECT-")
        }
    }
    return (selectedOption.value).toInt()
}


@Composable
fun inFields(text:String):Int{

    var items = remember{ mutableStateOf(0)}
    Row{
        Text(text)
        items.value = DropMenu().toInt()
    }
    return items.value
}


@Composable
fun makeOrder(ID: MutableState<Int>, b:Int, f:Int, d:Int, dArr:MutableList<String>, msg:MutableState<String>){
    val cScope = rememberCoroutineScope()

    val o: () -> Unit = {
        ID.value++
        cScope.launch{
            val currID = ID.value
            msg.value = "Your order ID is "+currID.toString()+".\nPlease collect on the right\nwhen your no. appears."
            order(currID, b, f, d)
            //Fix for neg index
            var out = currID % 8 - 1
            if (out == -1){
                out = 8
            }
            dArr.set(out, currID.toString())
        }
    }

    Button(onClick = o){
        Text("Order")
    }
}

@Composable
fun outFields(oArr:MutableList<String>){
    Column{
        for (j in 0..1) {

            Row {
                for (i in 0..3) {
                    var text = oArr.get(i+4*j).toString()
                    Button(onClick = {
                        oArr.set(i+4*j, "")
                    }){
                        Text(text)
                    }
                }
            }
        }


    }
}

@Composable
fun msgField(text:String){
    Text(text)
}

fun main() = application {
    val numArray = arrayOf("0", "1", "2", "3", "4", "5")

    Window(
        onCloseRequest = ::exitApplication,
        title = "HungryDonalds",
        state = rememberWindowState(width = 600.dp, height = 400.dp)
    ) {
        val count = remember { mutableStateOf(0) }
        val orderArray = remember { mutableStateListOf<String>("", "", "", "", "", "", "", "") }
        var mtext = remember{ mutableStateOf("")}

        var currBurgers = remember { mutableStateOf(0) }
        var currFries = remember { mutableStateOf(0) }
        var currDrinks = remember { mutableStateOf(0) }
        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                Row(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {

                    Column(Modifier, Arrangement.spacedBy(5.dp)) {
                        Column(Modifier, Arrangement.spacedBy(5.dp)) {
                            currBurgers.value = inFields("Burgers")
                            currFries.value = inFields("Fries")
                            currDrinks.value = inFields("Drinks")
                        }


                        makeOrder(
                            count,
                            currBurgers.value,
                            currFries.value,
                            currDrinks.value,
                            orderArray,
                            mtext
                        )
                        msgField(mtext.value)

                    }
                    outFields(orderArray)
                }
            }
        }
    }
}