package com.example.scoringtable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(0, 0)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5F5F5)
                ) {
                    ScoringTableApp()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun ScoringTableApp() {
    val rowNames = listOf("Раунд", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

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

    // Цвета для заголовков столбцов (100% непрозрачности)
    val headerColumnColors = listOf(
        Brush.horizontalGradient(listOf(Color(0xFFF5F5F5), Color(0xFFE0E0E0))),
        Brush.horizontalGradient(listOf(Color(0xFFFFF176), Color(0xFFFFEE58))),
        Brush.horizontalGradient(listOf(Color(0xFFCE93D8), Color(0xFFBA68C8))),
        Brush.horizontalGradient(listOf(Color(0xFF80DEEA), Color(0xFF4DD0E1))),
        Brush.horizontalGradient(listOf(Color(0xFFEF9A9A), Color(0xFFE57373))),
        Brush.horizontalGradient(listOf(Color(0xFFA5D6A7), Color(0xFF81C784))),
        Brush.horizontalGradient(listOf(Color(0xFFF5F5F5), Color(0xFFE0E0E0))),
        Brush.horizontalGradient(listOf(Color(0xFFF48FB1), Color(0xFFF06292)))
    )

    // Цвета для ячеек данных (50% прозрачности)
    val dataColumnColors = listOf(
        Color(0xFFF5F5F5).copy(alpha = 0.5f),
        Color(0xFFFFF176).copy(alpha = 0.5f),
        Color(0xFFCE93D8).copy(alpha = 0.5f),
        Color(0xFF80DEEA).copy(alpha = 0.5f),
        Color(0xFFEF9A9A).copy(alpha = 0.5f),
        Color(0xFFA5D6A7).copy(alpha = 0.5f),
        Color(0xFFF5F5F5).copy(alpha = 0.5f),
        Color(0xFFF48FB1).copy(alpha = 0.5f)
    )

    val rowCount = 11
    val colCount = 8

    val cellValues = remember {
        mutableStateMapOf<Pair<Int, Int>, String>()
    }

    var selectedRound by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        for (col in 0 until colCount) {
            cellValues[Pair(0, col)] = columnNames[col]
        }
        for (row in 0 until rowCount) {
            cellValues[Pair(row, 0)] = rowNames[row]
        }
        for (row in 1 until rowCount) {
            for (col in 1 until colCount) {
                if (cellValues[Pair(row, col)] == null) {
                    cellValues[Pair(row, col)] = ""
                }
            }
        }
    }

    var showClearDialog by remember { mutableStateOf(false) }

    fun calculateRoundSum(round: Int): Int {
        var total = 0
        for (col in 1..7) {
            val value = cellValues[Pair(round, col)]?.toIntOrNull() ?: 0
            total += value
        }
        return total
    }

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

    val grandTotal = remember {
        derivedStateOf {
            calculateGrandTotal()
        }
    }

    val selectedRoundSum = remember(selectedRound, cellValues) {
        if (selectedRound != null) {
            calculateRoundSum(selectedRound!!)
        } else {
            0
        }
    }

    fun clearTable() {
        for (row in 1..10) {
            for (col in 1..7) {
                cellValues[Pair(row, col)] = ""
            }
        }
        selectedRound = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(0.dp)
    ) {
        // Заголовок
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎲 Таблица подсчёта очков",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Таблица
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(0.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Шапка таблицы
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Угловая ячейка с вертикальным текстом "Раунд"
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(80.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            listOf("Р", "А", "У", "Н", "Д").forEach { letter ->
                                Text(
                                    text = letter,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    // Остальные заголовки столбцов
                    for (col in 1 until colCount) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                                .background(headerColumnColors[col]),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = columnNames[col],
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF333333),
                                fontWeight = FontWeight.Bold,
                                lineHeight = 13.sp,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Прокручиваемая область с данными
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    for (row in 1 until rowCount) {
                        val isSelected = selectedRound == row

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) Color(0xFFE3F2FD).copy(alpha = 0.8f)
                                    else Color.Transparent
                                )
                        ) {
                            // Столбец с номерами раундов - без тени и скруглений
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(56.dp)
                                    .background(
                                        if (isSelected) Color(0xFF2196F3)
                                        else dataColumnColors[0]
                                    )
                                    .clickable {
                                        selectedRound = if (selectedRound == row) null else row
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cellValues[Pair(row, 0)] ?: "",
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (isSelected) Color.White else Color(0xFF333333),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Остальные столбцы с небольшой тенью
                            for (col in 1 until colCount) {
                                val isEditable = col in 1..7

                                val cellValue = cellValues[Pair(row, col)] ?: ""

                                if (isEditable) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                            .padding(1.dp)
                                    ) {
                                        Card(
                                            modifier = Modifier.fillMaxSize(),
                                            shape = RoundedCornerShape(4.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = dataColumnColors[col]
                                            ),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                        ) {
                                            EditableCell(
                                                value = cellValue,
                                                onValueChange = { newValue ->
                                                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                        cellValues[Pair(row, col)] = newValue
                                                    }
                                                },
                                                modifier = Modifier.fillMaxSize(),
                                                backgroundColor = dataColumnColors[col]
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Нижняя панель
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(12.dp)
        ) {
            // Карточка общей суммы
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8EAF6)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "💰 Общая сумма всех очков:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = grandTotal.value.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            // Карточка суммы за выбранный раунд
            if (selectedRound != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🎯 Сумма за раунд $selectedRound:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = selectedRoundSum.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { showClearDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF5350)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("🗑️ Очистить таблицу", color = Color.White, fontSize = 16.sp)
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    "Подтверждение очистки",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Вы уверены, что хотите очистить все данные таблицы?")
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
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun EditableCell(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White
) {
    var localValue by remember(value) { mutableStateOf(value) }

    LaunchedEffect(value) {
        localValue = value
    }

    TextField(
        value = localValue,
        onValueChange = { newValue ->
            // Разрешаем только цифры
            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                localValue = newValue
                onValueChange(newValue)
            }
        },
        modifier = modifier,
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            color = Color(0xFF333333),
            fontWeight = FontWeight.Medium
        ),
        placeholder = {
            Text(
                "0",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(0.dp), // Убираем скругления, чтобы было больше места
        colors = TextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            disabledContainerColor = backgroundColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}