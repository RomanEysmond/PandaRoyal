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
    // Названия строк сверху-вниз: Раунд, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
    val rowNames = listOf("Раунд", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

    // Названия столбцов слева-направо (сокращенные для лучшего отображения)
    val columnNames = listOf(
        "Раунд",
        "Жёлтые",
        "Фиол. х2",
        "Голубые",
        "Красные",
        "Зелёные",
        "Белые",
        "Розовый",
        "Сумма"
    )

    // Полные названия для подсказок (можно добавить tooltip, но для простоты оставляем сокращенные)

    // Цвета ячеек в названии столбцов слева направо
    val columnBackgroundColors = listOf(
        Color.White,    // Раунд
        Color.Yellow,   // Сумма жёлтых
        Color(0xFFE1BEE7), // Сумма фиол. х2 (фиолетовый)
        Color.Cyan,     // Сумма голубых (голубой)
        Color.Red,      // Сумма красных
        Color.Green,    // Сумма зелёных
        Color.White,    // Сумма белых
        Color(0xFFFFC0CB), // Розовый
        Color.White     // Сумма за раунд
    )

    // 11 строк x 9 столбцов
    val rowCount = 11
    val colCount = 9

    // Данные таблицы - инициализируем сразу с заголовками
    val cells = remember {
        mutableStateListOf<MutableList<String>>().apply {
            for (row in 0 until rowCount) {
                val rowData = mutableListOf<String>()
                for (col in 0 until colCount) {
                    when {
                        row == 0 && col == 0 -> rowData.add(columnNames[0]) // "Раунд"
                        row == 0 -> rowData.add(columnNames[col]) // Заголовки столбцов
                        col == 0 -> rowData.add(rowNames[row]) // Заголовки строк
                        else -> rowData.add("") // Пустые ячейки для ввода
                    }
                }
                add(rowData)
            }
        }
    }

    var showClearDialog by remember { mutableStateOf(false) }

    // Функция обновления сумм за раунд
    fun updateRoundSums() {
        for (row in 1..10) {
            var total = 0
            for (col in 1..7) {
                val value = cells[row][col].toIntOrNull()
                if (value != null) {
                    total += value
                }
            }
            cells[row][8] = if (total > 0) total.toString() else ""
        }
    }

    // Функция очистки таблицы
    fun clearTable() {
        for (row in 1..10) {
            for (col in 1..7) {
                cells[row][col] = ""
            }
            cells[row][8] = ""
        }
    }

    // Общая сумма всех раундов
    val grandTotal = remember(cells) {
        (1..10).sumOf { row ->
            cells[row][8].toIntOrNull() ?: 0
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
            // Заголовок
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

            // Таблица с вертикальной прокруткой
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                for (row in 0 until rowCount) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (row == 0) Color.LightGray else Color.Transparent)
                    ) {
                        for (col in 0 until colCount) {
                            val isEditable = row in 1..10 && col in 1..7
                            val isTotalColumn = col == 8
                            val isFirstColumn = col == 0
                            val isHeaderRow = row == 0

                            val backgroundColor = if (isHeaderRow) {
                                columnBackgroundColors[col]
                            } else {
                                Color.White
                            }

                            val cellModifier = Modifier
                                .weight(1f)
                                .height(if (row == 0) 55.dp else 50.dp)
                                .border(0.5.dp, Color.Gray)
                                .background(backgroundColor)

                            when {
                                // Заголовки, первый столбец и столбец суммы
                                isHeaderRow || isFirstColumn || isTotalColumn -> {
                                    Box(
                                        modifier = cellModifier.padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cells[row][col],
                                            fontSize = if (isHeaderRow) 11.sp else 13.sp,
                                            textAlign = TextAlign.Center,
                                            color = Color.Black,
                                            fontWeight = if (row == 0) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = if (isHeaderRow) 2 else 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                // Редактируемые ячейки
                                isEditable -> {
                                    TextField(
                                        value = cells[row][col],
                                        onValueChange = { newValue ->
                                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                cells[row][col] = newValue
                                                updateRoundSums()
                                            }
                                        },
                                        modifier = cellModifier,
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
                            }
                        }
                    }
                }
            }

            // Нижняя панель с итогом и кнопкой
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                // Итоговая сумма
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
                            text = "Итоговая сумма:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = grandTotal.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Кнопка очистки
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

    // Диалог подтверждения очистки
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