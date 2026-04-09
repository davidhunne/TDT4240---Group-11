import express from "express";
import playerRoutes from "./routes/playerRoutes";
import gameRoutes from "./routes/gameRoutes";

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());

app.get("/health", (_req, res) => {
  res.json({ status: "ok" });
});

app.use("/api/players", playerRoutes);
app.use("/api/games", gameRoutes);

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});

export default app;
