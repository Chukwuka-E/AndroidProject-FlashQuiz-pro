package com.example.moodlift20

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                onCategorySelected = { category -> navController.navigate("quiz/$category") }
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
                onRestart = { navController.navigate("category") { popUpTo(0) } }
            )
        }
    }
}

@Composable
fun CategorySelectionScreen(onCategorySelected: (String) -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Text(
                text = "FlashQuiz Pro",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
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
            Text("Select a Category", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            questionsByCategory.keys.forEach { category ->
                Button(
                    onClick = { onCategorySelected(category) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(category)
                }
            }
        }
    }
}

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
            Text(
                text = "$category Quiz",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
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
                    color = if (timeLeft <= 3) Color.Red else Color.Black
                )
            }
            Text(
                text = currentQuestion.text,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
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
                        .padding(vertical = 4.dp)
                ) {
                    Text(option)
                }
            }
        }
    }
}

@Composable
fun ResultsScreen(
    score: Int,
    total: Int,
    category: String,
    sharedPreferences: SharedPreferences,
    onRestart: () -> Unit
) {
    var highScore by remember { mutableStateOf(sharedPreferences.getInt("highScore", 0)) }
    val isNewHighScore = score > highScore

    if (isNewHighScore) {
        highScore = score
        sharedPreferences.edit { putInt("highScore", highScore) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Text(
                text = "Quiz Results",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
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
            Text(
                text = "You scored $score out of $total",
                style = MaterialTheme.typography.headlineSmall
            )
            if (isNewHighScore) {
                Text(
                    text = "New High Score! 🎉",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Green
                )
            } else {
                Text(
                    text = "High Score: $highScore",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = motivations[Random.nextInt(motivations.size)],
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Blue
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onRestart) {
                Text("Restart Quiz")
            }
        }
    }
}