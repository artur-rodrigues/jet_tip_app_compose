package com.example.jettipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.HorizontalSpacer
import com.example.jettipapp.components.InputField
import com.example.jettipapp.components.TipText
import com.example.jettipapp.components.VerticalSpacer
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.util.calculateTotalPerPerson
import com.example.jettipapp.util.calculateTotalTip
import com.example.jettipapp.util.cleanValueString
import com.example.jettipapp.widget.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Content()
}


@Composable
fun Content() {
    val totalPerPersonState = remember {
        mutableDoubleStateOf(0.0)
    }

    MyApp {
        TopHeader(totalPerPersonState.doubleValue)
        BillForm {
            totalPerPersonState.doubleValue = it
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column (modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .padding(12.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {

        Column(modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

            val total = "R$ %.2f".format(totalPerPerson)

            Text(text = "Total Per Person",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(text = total,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun BillForm(modifier: Modifier = Modifier,
             onValChange: (Double) -> Unit = {}) {

    val totalBillState = remember {
        mutableStateOf("")
    }

    val splitByState = remember {
        mutableIntStateOf(1)
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().run {
            isNotEmpty() && cleanValueString(this).toInt() > 0
        }
    }

    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }

    val keyBoardController = LocalSoftwareKeyboardController.current

    Surface(modifier = modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)) {

        Column(modifier = Modifier.padding(start = 15.dp, top = 10.dp, end = 15.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {

            InputField(valueState = totalBillState,
                labelId = "Enter Bill",
                onAction = KeyboardActions {
                    keyBoardController?.hide()
                    if(!validState) return@KeyboardActions
                })

            if(validState) {
                SplitTipRow(splitByState.intValue) {
                    splitByState.intValue = it
                }

                TipRow(calculateTotalTip(totalBillState.value, sliderPositionState.floatValue.toInt()))

                SliderRow(sliderPositionState.floatValue) {
                    sliderPositionState.floatValue = it
                }

            } else {
                Box {}
            }
        }
    }

    onValChange(calculateTotalPerPerson(totalBillState.value, splitByState.intValue, sliderPositionState.floatValue.toInt()))
}

@Composable
fun SplitTipRow(splitBy: Int, updateCount: (Int) -> Unit) {
    Row(modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {

        TipText("Split")

        Row(modifier = Modifier
            .padding(horizontal = 3.dp),
            horizontalArrangement = Arrangement.End) {

            RoundIconButton(
                modifier = Modifier.padding(end = 9.dp),
                imageVector = Icons.Default.Remove,
                onClick = {
                    if(splitBy > 1) {
                        updateCount(splitBy - 1)
                    }
                })

            TipText("$splitBy")

            RoundIconButton(
                modifier = Modifier.padding(start = 9.dp),
                imageVector = Icons.Default.Add,
                onClick = {
                    updateCount(splitBy + 1)
                })
        }
    }
}

@Composable
fun TipRow(tipAmount: Double) {
    Row(modifier = Modifier
        .padding(top = 12.dp)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {

        TipText("Tip")

        HorizontalSpacer(200)

        TipText("R$ %.2f".format(tipAmount))
    }
}

@Composable
private fun SliderRow(sliderPosition: Float, onChangeValue: (Float) -> Unit) {
    Column(modifier = Modifier.padding(top = 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "${sliderPosition.toInt()} %")

        VerticalSpacer(14)

        Slider(
            modifier = Modifier.padding(start = 6.dp, end = 6.dp),
            value = sliderPosition,
            valueRange = 0f..100f,
            onValueChange = {onChangeValue(it)})
    }
}