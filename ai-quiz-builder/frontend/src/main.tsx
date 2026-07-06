import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrainCircuit, Check, Download, Loader2, RefreshCcw, Sparkles } from 'lucide-react';
import './styles.css';

type QuizQuestion = {
  prompt: string;
  options: string[];
  answerIndex: number;
  explanation: string;
};

type Quiz = {
  title: string;
  topic: string;
  difficulty: string;
  questions: QuizQuestion[];
};

const API_BASE = import.meta.env.VITE_API_BASE ?? '';

function App() {
  const [topic, setTopic] = React.useState('Spring Boot REST APIs');
  const [difficulty, setDifficulty] = React.useState('medium');
  const [questionCount, setQuestionCount] = React.useState(5);
  const [quiz, setQuiz] = React.useState<Quiz | null>(null);
  const [selected, setSelected] = React.useState<Record<number, number>>({});
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState('');

  const score = quiz
    ? quiz.questions.reduce((total, question, index) => (
        selected[index] === question.answerIndex ? total + 1 : total
      ), 0)
    : 0;

  async function generateQuiz(event: React.FormEvent) {
    event.preventDefault();
    setLoading(true);
    setError('');
    setSelected({});

    try {
      const response = await fetch(`${API_BASE}/api/quizzes/generate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ topic, difficulty, questionCount })
      });

      if (!response.ok) {
        throw new Error('Quiz generation failed');
      }

      setQuiz(await response.json());
    } catch {
      setError('Could not generate a quiz. Check that the Spring Boot API is running.');
    } finally {
      setLoading(false);
    }
  }

  function exportQuiz() {
    if (!quiz) return;
    const blob = new Blob([JSON.stringify(quiz, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${quiz.topic.toLowerCase().replace(/[^a-z0-9]+/g, '-')}-quiz.json`;
    link.click();
    URL.revokeObjectURL(url);
  }

  return (
    <main className="shell">
      <section className="workspace">
        <aside className="builder-panel">
          <div className="brand">
            <BrainCircuit aria-hidden="true" />
            <div>
              <h1>AI Quiz Builder</h1>
              <p>Create focused quizzes for lessons, onboarding, and revision.</p>
            </div>
          </div>

          <form onSubmit={generateQuiz} className="quiz-form">
            <label>
              Topic
              <input
                value={topic}
                onChange={(event) => setTopic(event.target.value)}
                placeholder="e.g. Java streams"
                required
              />
            </label>

            <label>
              Difficulty
              <select value={difficulty} onChange={(event) => setDifficulty(event.target.value)}>
                <option value="easy">Easy</option>
                <option value="medium">Medium</option>
                <option value="hard">Hard</option>
              </select>
            </label>

            <label>
              Questions
              <input
                type="number"
                min="1"
                max="12"
                value={questionCount}
                onChange={(event) => setQuestionCount(Number(event.target.value))}
              />
            </label>

            <button className="primary-button" disabled={loading || !topic.trim()}>
              {loading ? <Loader2 className="spin" aria-hidden="true" /> : <Sparkles aria-hidden="true" />}
              {loading ? 'Generating' : 'Generate quiz'}
            </button>
          </form>

          {error && <p className="error">{error}</p>}
        </aside>

        <section className="quiz-stage" aria-live="polite">
          {!quiz ? (
            <div className="empty-state">
              <Sparkles aria-hidden="true" />
              <h2>Ready when you are.</h2>
              <p>Choose a topic and generate a quiz. Answers and explanations appear after each choice.</p>
            </div>
          ) : (
            <>
              <header className="quiz-header">
                <div>
                  <p>{quiz.difficulty} difficulty</p>
                  <h2>{quiz.title}</h2>
                </div>
                <div className="actions">
                  <button type="button" onClick={exportQuiz} title="Download quiz JSON">
                    <Download aria-hidden="true" />
                  </button>
                  <button type="button" onClick={() => setSelected({})} title="Reset answers">
                    <RefreshCcw aria-hidden="true" />
                  </button>
                </div>
              </header>

              <div className="score-bar">
                <span>Score</span>
                <strong>{score}/{quiz.questions.length}</strong>
              </div>

              <div className="questions">
                {quiz.questions.map((question, questionIndex) => {
                  const chosen = selected[questionIndex];
                  return (
                    <article className="question-card" key={`${question.prompt}-${questionIndex}`}>
                      <h3>{questionIndex + 1}. {question.prompt}</h3>
                      <div className="options">
                        {question.options.map((option, optionIndex) => {
                          const isChosen = chosen === optionIndex;
                          const isCorrect = question.answerIndex === optionIndex;
                          const className = [
                            'option-button',
                            isChosen ? 'chosen' : '',
                            chosen !== undefined && isCorrect ? 'correct' : '',
                            isChosen && !isCorrect ? 'wrong' : ''
                          ].join(' ');

                          return (
                            <button
                              className={className}
                              type="button"
                              key={option}
                              onClick={() => setSelected({ ...selected, [questionIndex]: optionIndex })}
                            >
                              <span>{option}</span>
                              {chosen !== undefined && isCorrect && <Check aria-hidden="true" />}
                            </button>
                          );
                        })}
                      </div>
                      {chosen !== undefined && <p className="explanation">{question.explanation}</p>}
                    </article>
                  );
                })}
              </div>
            </>
          )}
        </section>
      </section>
    </main>
  );
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

