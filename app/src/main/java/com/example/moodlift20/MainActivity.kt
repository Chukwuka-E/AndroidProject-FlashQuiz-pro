package com.example.moodlift20

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moodlift20.ui.theme.MoodLift20Theme
import kotlinx.coroutines.delay
import androidx.core.content.edit
import kotlin.random.Random
import kotlin.collections.*

data class Question(val text: String, val options: List<String>, val correctIndex: Int)

val questionsByCategory = mapOf(
    "Tech" to listOf(
        Question("What does CPU stand for?", listOf("Central Processing Unit", "Computer Personal Unit", "Central Power Unit", "Control Processing Unit"), 0),
        Question("What is the main language for Android apps?", listOf("Java", "Kotlin", "Python", "C++"), 1),
        Question("Who founded Microsoft?", listOf("Steve Jobs", "Bill Gates", "Mark Zuckerberg", "Elon Musk"), 1),
        Question("What does HTML stand for?", listOf("HyperText Markup Language", "HighTech Machine Language", "HyperTool Multi Language", "Home Text Markup Language"), 0),
        Question("Which company developed the iPhone?", listOf("Samsung", "Google", "Apple", "Nokia"), 2),
        Question("What is the purpose of a firewall?", listOf("Data storage", "Network security", "File compression", "Hardware cooling"), 1),
        Question("What does RAM stand for?", listOf("Read Access Memory", "Random Access Memory", "Run Application Module", "Remote Access Machine"), 1),
        Question("Which protocol is used for secure web browsing?", listOf("HTTP", "FTP", "HTTPS", "SMTP"), 2),
        Question("What is the name of Google's mobile OS?", listOf("iOS", "Android", "Windows", "Linux"), 1),
        Question("Who is the CEO of Tesla?", listOf("Tim Cook", "Sundar Pichai", "Elon Musk", "Jeff Bezos"), 2)
    ),
    "Sports" to listOf(
        Question("How many players are on a soccer team?", listOf("9", "10", "11", "12"), 2),
        Question("In which sport is the term 'home run' used?", listOf("Cricket", "Baseball", "Tennis", "Football"), 1),
        Question("Who is known as the 'King of Football'?", listOf("Lionel Messi", "Cristiano Ronaldo", "Pele", "Diego Maradona"), 2),
        Question("Which country won the FIFA World Cup in 2018?", listOf("Brazil", "Germany", "France", "Argentina"), 2),
        Question("What is the maximum score in a single bowling game?", listOf("200", "250", "300", "350"), 2),
        Question("In tennis, what is a score of zero called?", listOf("Love", "Ace", "Deuce", "Fault"), 0),
        Question("Which sport uses a shuttlecock?", listOf("Tennis", "Badminton", "Squash", "Table Tennis"), 1),
        Question("How many rings are in the Olympic logo?", listOf("3", "4", "5", "6"), 2),
        Question("Who holds the record for most Olympic gold medals?", listOf("Usain Bolt", "Michael Phelps", "Simone Biles", "Carl Lewis"), 1),
        Question("What is the distance of a marathon?", listOf("26.2 miles", "20 miles", "30 miles", "15 miles"), 0)
    ),
    "General Knowledge" to listOf(
        Question("What is the capital of France?", listOf("Berlin", "Madrid", "Paris", "Rome"), 2),
        Question("How many continents are there?", listOf("5", "6", "7", "8"), 2),
        Question("What is the largest planet in our solar system?", listOf("Earth", "Mars", "Jupiter", "Saturn"), 2),
        Question("Who painted the Mona Lisa?", listOf("Vincent van Gogh", "Leonardo da Vinci", "Pablo Picasso", "Claude Monet"), 1),
        Question("What is the currency of Japan?", listOf("Yen", "Won", "Dollar", "Euro"), 0),
        Question("Which gas is most abundant in Earth's atmosphere?", listOf("Oxygen", "Nitrogen", "Carbon Dioxide", "Helium"), 1),
        Question("What is the smallest country in the world?", listOf("Monaco", "Vatican City", "San Marino", "Liechtenstein"), 1),
        Question("Who wrote 'Romeo and Juliet'?", listOf("William Shakespeare", "Charles Dickens", "Jane Austen", "Mark Twain"), 0),
        Question("What is the longest river in the world?", listOf("Nile", "Amazon", "Yangtze", "Mississippi"), 0),
        Question("Which animal is known as the 'King of the Jungle'?", listOf("Tiger", "Lion", "Elephant", "Gorilla"), 1)
    ),
    "History" to listOf(
        Question("Who was the first President of the United States?", listOf("Abraham Lincoln", "George Washington", "Thomas Jefferson", "John Adams"), 1),
        Question("In which year did World War II end?", listOf("1943", "1944", "1945", "1946"), 2),
        Question("What ancient wonder was located in Egypt?", listOf("Colossus of Rhodes", "Great Pyramid of Giza", "Hanging Gardens", "Temple of Artemis"), 1),
        Question("Who was known as the 'Iron Lady'?", listOf("Angela Merkel", "Margaret Thatcher", "Indira Gandhi", "Golda Meir"), 1),
        Question("Which empire built the Colosseum?", listOf("Greek", "Roman", "Ottoman", "Byzantine"), 1),
        Question("What was the name of the ship that sank in 1912?", listOf("Lusitania", "Titanic", "Britannic", "Olympic"), 1),
        Question("Who discovered America in 1492?", listOf("Christopher Columbus", "Ferdinand Magellan", "Vasco da Gama", "Marco Polo"), 0),
        Question("Which war was fought between the North and South of the USA?", listOf("World War I", "Civil War", "Revolutionary War", "War of 1812"), 1),
        Question("Who was the first woman to fly solo across the Atlantic?", listOf("Amelia Earhart", "Bessie Coleman", "Harriet Quimby", "Jacqueline Cochran"), 0),
        Question("In which century was the Renaissance?", listOf("13th", "14th", "15th", "16th"), 2)
    ),
    "Science" to listOf(
        Question("What is H2O commonly known as?", listOf("Sugar", "Salt", "Water", "Oxygen"), 2),
        Question("What planet is known as the Red Planet?", listOf("Venus", "Mars", "Mercury", "Jupiter"), 1),
        Question("What is the chemical symbol for gold?", listOf("Au", "Ag", "Fe", "Cu"), 0),
        Question("Which scientist proposed the theory of relativity?", listOf("Isaac Newton", "Albert Einstein", "Galileo Galilei", "Stephen Hawking"), 1),
        Question("What gas do plants absorb during photosynthesis?", listOf("Oxygen", "Nitrogen", "Carbon Dioxide", "Helium"), 2),
        Question("What is the primary source of energy for Earth's climate?", listOf("Moon", "Sun", "Wind", "Ocean"), 1),
        Question("Which organ pumps blood in the human body?", listOf("Liver", "Heart", "Lungs", "Kidneys"), 1),
        Question("What is the speed of light in a vacuum?", listOf("300,000 km/s", "150,000 km/s", "450,000 km/s", "600,000 km/s"), 0),
        Question("What is the main source of energy for Earth's crust?", listOf("Solar", "Geothermal", "Nuclear", "Wind"), 1),
        Question("Which element has the atomic number 1?", listOf("Helium", "Hydrogen", "Lithium", "Carbon"), 1)
    )
)

val motivations = listOf(
    "Great effort! Keep shining!",
    "You're unstoppable! Try again!",
    "Every answer brings you closer to mastery!",
    "Well done! Your knowledge is growing!",
    "Stay curious and keep learning!"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodLift20Theme {
                FlashQuizApp()
            }
        }
    }
}

@Composable
fun FlashQuizApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("FlashQuizPrefs", android.content.Context.MODE_PRIVATE)
    }

    NavHost(navController = navController, startDestination = "category") {
        composable("category") {
            CategorySelectionScreen(
                onCategorySelected = { category -> navController.navigate("quiz/$category") },
                navController = navController
            )
        }
        composable("quiz/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "General Knowledge"
            QuizScreen(
                category = category,
                sharedPreferences = sharedPreferences,
                onQuizFinished = { score, total ->
                    navController.navigate("results/$score/$total/$category")
                }
            )
        }
        composable("results/{score}/{total}/{category}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
            val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 0
            val category = backStackEntry.arguments?.getString("category") ?: "General Knowledge"
            ResultsScreen(
                score = score,
                total = total,
                category = category,
                sharedPreferences = sharedPreferences,
                onRestart = { navController.navigate("category") { popUpTo(0) } },
                navController = navController
            )
        }
        composable("about") {
            AboutScreen(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavHostController) {
    // List of group members (name and matric number)
    val groupMembers = listOf(
        Pair("Emenike Chukwuka", "22/000"),
        Pair("Majeed Alloh", "22/3333"),
        Pair("John Smith", "22/1111"),
        Pair("Sarah Johnson", "22/2222"),
        Pair("Michael Brown", "22/4444"),
        Pair("Emily Davis", "22/5555"),
        Pair("David Wilson", "22/6666"),
        Pair("Jessica Taylor", "22/7777"),
        Pair("Daniel Anderson", "22/8888"),
        Pair("Olivia Martinez", "22/9999")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("About FlashQuiz") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // App icon placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
                Text(
                    "Q",
                    fontSize = 64.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "FlashQuiz Pro",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "A fun quiz app to test your knowledge across various categories. Challenge yourself and beat your high scores!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Group members section
            Text(
                "Group Members:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Display group members in a grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth()
                    .height(400.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(groupMembers) { member ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = member.first,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Matric: ${member.second}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Feature list
            Text(
                "Features:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                FeatureItem("Multiple quiz categories")
                FeatureItem("Timed questions")
                FeatureItem("Score tracking")
                FeatureItem("Motivational feedback")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Version 1.0.0",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionScreen(
    onCategorySelected: (String) -> Unit,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FlashQuiz Pro") },
                actions = {
                    IconButton(onClick = { navController.navigate("about") }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "About",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Select a Category",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            questionsByCategory.keys.forEach { category ->
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    onClick = { onCategorySelected(category) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                category.first().toString(),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            category,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Start quiz",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    category: String,
    sharedPreferences: SharedPreferences,
    onQuizFinished: (Int, Int) -> Unit
) {
    val baseQuestions = questionsByCategory[category] ?: emptyList()
    val questions = remember { mutableStateOf(baseQuestions.shuffled()) }.value
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(10) }
    var answered by remember { mutableStateOf(false) }

    LaunchedEffect(currentQuestionIndex) {
        answered = false
        timeLeft = 10
        while (timeLeft > 0 && !answered) {
            delay(1000L)
            timeLeft -= 1
        }
        if (!answered) {
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex += 1
            } else {
                onQuizFinished(score, questions.size)
            }
        }
    }

    if (questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No questions available")
        }
        return
    }

    val currentQuestion = questions[currentQuestionIndex]

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("$category Quiz") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Question ${currentQuestionIndex + 1}/${questions.size}",
                    fontSize = 18.sp
                )
                Text(
                    text = "Time: $timeLeft s",
                    fontSize = 18.sp,
                    color = if (timeLeft <= 3) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }

            // Timer progress bar
            LinearProgressIndicator(
                progress = timeLeft / 10f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = when {
                    timeLeft > 6 -> Color.Green
                    timeLeft > 3 -> Color.Yellow
                    else -> Color.Red
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentQuestion.text,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            currentQuestion.options.forEachIndexed { index, option ->
                Button(
                    onClick = {
                        if (!answered) {
                            answered = true
                            if (index == currentQuestion.correctIndex) {
                                score += 1
                            }
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex += 1
                            } else {
                                onQuizFinished(score, questions.size)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(option)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    score: Int,
    total: Int,
    category: String,
    sharedPreferences: SharedPreferences,
    onRestart: () -> Unit,
    navController: NavHostController
) {
    var highScore by remember { mutableStateOf(sharedPreferences.getInt("highScore", 0)) }
    val isNewHighScore = score > highScore

    if (isNewHighScore) {
        highScore = score
        sharedPreferences.edit { putInt("highScore", highScore) }
    }

    val percentage = (score.toFloat() / total.toFloat()) * 100
    val resultMessage = when {
        percentage >= 90 -> "Genius! 🎓"
        percentage >= 70 -> "Great job! 👍"
        percentage >= 50 -> "Good effort! 💪"
        else -> "Keep practicing! 📚"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quiz Results") },
                actions = {
                    IconButton(onClick = { navController.navigate("about") }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "About",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Score circle
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .border(
                        BorderStroke(8.dp, MaterialTheme.colorScheme.primary),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "out of $total",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = resultMessage,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = motivations[Random.nextInt(motivations.size)],
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isNewHighScore) {
                Text(
                    text = "🎉 New High Score! 🎉",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Green
                )
            } else {
                Text(
                    text = "High Score: $highScore",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Restart Quiz", fontSize = 18.sp)
            }
        }
    }
}