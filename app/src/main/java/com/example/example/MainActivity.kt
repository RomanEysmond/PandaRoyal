package com.example.scoringtable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScoringTableApp()
                }
            }
        }
    }
}

@Composable
fun ScoringTableApp() {
    val rowNames = listOf("Раунд", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

    // Названия столбцов
    val columnNames = listOf(
        "Раунд",
        "Сумма\nжёлт.",
        "Сумма фиол.\nх2",
        "Сумма голуб.\n(х2,с\n блест.)",
        "Сумма красн.\nхКол. Красн.",
        "Сумма\nзелён.",
        "Сумма\nбелых",
        "Розов."
    )

    // Цвета ячеек
    val columnBackgroundColors = listOf(
        Color.White,
        Color.Yellow,
        Color(0xFFE1BEE7),
        Color.Cyan,
        Color.Red,
        Color.Green,
        Color.White,
        Color(0xFFFFC0CB)
    )

    val rowCount = 11
    val colCount = 8

    // Используем mutableStateMapOf для хранения данных с ключом Pair<row, col>
    val cellValues = remember {
        mutableStateMapOf<Pair<Int, Int>, String>()
    }

    // Инициализация начальных значений при первом запуске
    LaunchedEffect(Unit) {
        // Заполняем заголовки столбцов (первая строка)
        for (col in 0 until colCount) {
            cellValues[Pair(0, col)] = columnNames[col]
        }
        // Заполняем заголовки строк (первый столбец)
        for (row in 0 until rowCount) {
            cellValues[Pair(row, 0)] = rowNames[row]
        }
        // Заполняем остальные ячейки пустыми строками
        for (row in 1 until rowCount) {
            for (col in 1 until colCount) {
                if (cellValues[Pair(row, col)] == null) {
                    cellValues[Pair(row, col)] = ""
                }
            }
        }
    }

    var showClearDialog by remember { mutableStateOf(false) }

    // Функция для вычисления общей суммы всех чисел за все раунды
    fun calculateGrandTotal(): Int {
        var total = 0
        for (row in 1..10) {
            for (col in 1..7) {
                val value = cellValues[Pair(row, col)]?.toIntOrNull() ?: 0
                total += value
            }
        }
        return total
    }

    // Общая сумма всех очков - пересчитывается при каждом изменении
    val grandTotal = remember {
        derivedStateOf {
            calculateGrandTotal()
        }
    }

    // Функция очистки таблицы
    fun clearTable() {
        for (row in 1..10) {
            for (col in 1..7) {
                cellValues[Pair(row, col)] = ""
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {
            Text(
                text = "Таблица подсчёта очков",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            // Закрепленная шапка таблицы (первая строка)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .border(0.5.dp, Color.Gray)
            ) {
                for (col in 0 until colCount) {
                    val backgroundColor = columnBackgroundColors[col]

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(70.dp)
                            .border(0.5.dp, Color.Gray)
                            .background(backgroundColor)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = columnNames[col],
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 11.sp,
                            maxLines = 4,
                            overflow = TextOverflow.Visible
                        )
                    }
                }
            }

            // Прокручиваемая область с данными (строки 1-10)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                for (row in 1 until rowCount) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                    ) {
                        for (col in 0 until colCount) {
                            val isEditable = col in 1..7 // Все столбцы кроме первого
                            val isFirstColumn = col == 0

                            val backgroundColor = Color.White

                            val cellModifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .border(0.5.dp, Color.Gray)
                                .background(backgroundColor)

                            val cellValue = cellValues[Pair(row, col)] ?: ""

                            when {
                                isFirstColumn -> {
                                    // Первый столбец (номера раундов) - только текст
                                    Box(
                                        modifier = cellModifier.padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cellValue,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            color = Color.Black,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }
                                isEditable -> {
                                    // Редактируемые ячейки
                                    EditableCell(
                                        value = cellValue,
                                        onValueChange = { newValue ->
                                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                cellValues[Pair(row, col)] = newValue
                                            }
                                        },
                                        modifier = cellModifier
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Нижняя панель с итоговой суммой и кнопкой очистки
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                // Карточка с итоговой суммой
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Общая сумма всех очков:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = grandTotal.value.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Очистить таблицу", color = Color.White, fontSize = 15.sp)
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    "Подтверждение очистки",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Вы уверены, что хотите очистить все данные таблицы?",
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clearTable()
                        showClearDialog = false
                    }
                ) {
                    Text("Очистить", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Отмена", color = Color.Black)
                }
            }
        )
    }
}

@Composable
fun EditableCell(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var localValue by remember(value) { mutableStateOf(value) }

    LaunchedEffect(value) {
        localValue = value
    }

    TextField(
        value = localValue,
        onValueChange = { newValue ->
            localValue = newValue
            onValueChange(newValue)
        },
        modifier = modifier,
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            color = Color.Black
        ),
        placeholder = {
            Text(
                "0",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(0.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}