"use client";

import { FormEvent, useEffect, useMemo, useState } from "react";

type Price = { symbol: string; price: number; change24h: number; volume: number; timestamp: string };
type Position = { symbol: string; quantity: number; averageEntryPrice: number; marketPrice: number; marketValue: number; unrealizedPnl: number };
type Portfolio = { cashBalance: number; marketValue: number; totalValue: number; positions: Position[] };
type Order = { id: number; symbol: string; side: "BUY" | "SELL"; quantity: number; executedPrice: number | null; notional: number; status: string; createdAt: string };

const apiUrl = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";
const money = (value: number | null | undefined) => value == null ? "--" : `$${value.toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

export default function Home() {
  const [prices, setPrices] = useState<Record<string, Price>>({});
  const [portfolio, setPortfolio] = useState<Portfolio>({ cashBalance: 10000, marketValue: 0, totalValue: 10000, positions: [] });
  const [orders, setOrders] = useState<Order[]>([]);
  const [symbol, setSymbol] = useState("BTCUSDT");
  const [side, setSide] = useState<"BUY" | "SELL">("BUY");
  const [quantity, setQuantity] = useState("0.001");
  const [message, setMessage] = useState("Connecting to paper execution");
  const [strategyEnabled, setStrategyEnabled] = useState(false);

  async function refresh() {
    try {
      const [priceResponse, portfolioResponse, orderResponse] = await Promise.all([
        fetch(`${apiUrl}/api/crypto/prices`, { cache: "no-store" }),
        fetch(`${apiUrl}/api/portfolio`, { cache: "no-store" }),
        fetch(`${apiUrl}/api/trading/orders`, { cache: "no-store" }),
      ]);
      if (!priceResponse.ok || !portfolioResponse.ok || !orderResponse.ok) throw new Error("API unavailable");
      setPrices(await priceResponse.json());
      setPortfolio(await portfolioResponse.json());
      setOrders((await orderResponse.json()).slice(-8).reverse());
      const strategyResponse = await fetch(`${apiUrl}/api/strategy`, { cache: "no-store" });
      if (strategyResponse.ok) setStrategyEnabled((await strategyResponse.json()).enabled);
      setMessage("Live market data connected");
    } catch { setMessage("Backend offline · start Spring Boot on port 8080"); }
  }

  useEffect(() => { refresh(); const timer = window.setInterval(refresh, 5000); return () => window.clearInterval(timer); }, []);

  async function submitOrder(event: FormEvent) {
    event.preventDefault();
    setMessage("Submitting paper order...");
    try {
      const response = await fetch(`${apiUrl}/api/trading/orders`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ symbol, side, quantity: Number(quantity) }) });
      const order = await response.json();
      setMessage(order.status === "FILLED" ? `Filled ${side} ${quantity} ${symbol}` : order.rejectionReason ?? "Order rejected");
      await refresh();
    } catch { setMessage("Could not reach the execution service"); }
  }

  async function toggleStrategy() {
    const enabled = !strategyEnabled;
    setStrategyEnabled(enabled);
    await fetch(`${apiUrl}/api/strategy/enabled?enabled=${enabled}`, { method: "POST" });
  }

  const selectedPrice = prices[symbol]?.price;
  const totalPnl = useMemo(() => portfolio.positions.reduce((sum, position) => sum + position.unrealizedPnl, 0), [portfolio.positions]);
  const tracked = ["BTCUSDT", "ETHUSDT", "BNBUSDT"];

  return (
    <main className="shell">
      <header className="topbar"><div className="brand"><span className="brand-mark">C</span><div><strong>CRYPTOWATCH</strong><span>paper trading terminal</span></div></div><div className="status"><span className="pulse" /> {message}</div><button className="avatar" aria-label="Account menu">MH</button></header>
      <section className="intro"><div><p className="eyebrow">MARKET OPERATIONS / 01</p><h1>Stay ahead<br /><em>of the tape.</em></h1><p className="lede">A focused command center for real-time crypto prices, paper execution, and portfolio health.</p></div><div className="session"><span>SESSION VALUE</span><strong>{money(portfolio.totalValue)}</strong><small><b className={totalPnl >= 0 ? "positive" : "negative"}>{totalPnl >= 0 ? "+" : ""}{money(totalPnl)}</b> unrealized today</small></div></section>
      <section className="ticker-grid">{tracked.map((key) => { const item = prices[key]; return <article className="ticker" key={key}><div className="ticker-head"><span>{key.replace("USDT", "")}</span><span className="coin-pip" /></div><strong>{item ? money(item.price) : "--"}</strong><span className={item?.change24h >= 0 ? "positive" : "negative"}>{item ? `${item.change24h >= 0 ? "+" : ""}${item.change24h.toFixed(2)}%` : "Waiting for feed"}</span><div className="sparkline"><i /><i /><i /><i /><i /><i /><i /></div></article>; })}</section>
      <div className="workspace"><section className="panel market-panel"><div className="panel-title"><div><span className="eyebrow">MARKET PULSE</span><h2>Tracked assets</h2></div><span className="live-tag"><span className="pulse" /> LIVE</span></div><div className="asset-table"><div className="table-head"><span>Asset</span><span>Last price</span><span>24h move</span><span>Volume</span></div>{Object.values(prices).map((item) => <div className="asset-row" key={item.symbol}><span className="asset-name"><b>{item.symbol.slice(0, -4)}</b><small>/ USDT</small></span><strong>{money(item.price)}</strong><span className={item.change24h >= 0 ? "positive" : "negative"}>{item.change24h >= 0 ? "+" : ""}{item.change24h.toFixed(2)}%</span><span className="muted">{item.volume.toLocaleString()}</span></div>)}{Object.keys(prices).length === 0 && <div className="empty">Waiting for live quotes...</div>}</div></section><section className="panel order-panel"><div className="panel-title"><div><span className="eyebrow">EXECUTION</span><h2>Place paper order</h2></div><span className="paper-tag">PAPER</span></div><form onSubmit={submitOrder}><div className="segmented"><button type="button" className={side === "BUY" ? "active buy" : ""} onClick={() => setSide("BUY")}>Buy</button><button type="button" className={side === "SELL" ? "active sell" : ""} onClick={() => setSide("SELL")}>Sell</button></div><label>Market<select value={symbol} onChange={(event) => setSymbol(event.target.value)}><option>BTCUSDT</option><option>ETHUSDT</option><option>BNBUSDT</option></select></label><label>Quantity<input type="number" min="0.00000001" step="any" value={quantity} onChange={(event) => setQuantity(event.target.value)} /></label><div className="estimate"><span>Estimated notional</span><strong>{selectedPrice ? money(selectedPrice * Number(quantity)) : "--"}</strong></div><button className={`submit ${side.toLowerCase()}`} type="submit">Review {side.toLowerCase()} order <span>↗</span></button></form></section></div>
      <div className="lower-grid"><section className="panel portfolio-panel"><div className="panel-title"><div><span className="eyebrow">ACCOUNT OVERVIEW</span><h2>Portfolio</h2></div><span className="muted">Paper account</span></div><div className="metrics"><div><span>Available cash</span><strong>{money(portfolio.cashBalance)}</strong></div><div><span>Invested value</span><strong>{money(portfolio.marketValue)}</strong></div><div><span>Unrealized P&amp;L</span><strong className={totalPnl >= 0 ? "positive" : "negative"}>{money(totalPnl)}</strong></div></div><div className="position-list">{portfolio.positions.length ? portfolio.positions.map((position) => <div className="position" key={position.symbol}><span><b>{position.symbol}</b><small>{position.quantity} units</small></span><strong>{money(position.marketValue)}</strong><span className={position.unrealizedPnl >= 0 ? "positive" : "negative"}>{position.unrealizedPnl >= 0 ? "+" : ""}{money(position.unrealizedPnl)}</span></div>) : <div className="empty">No open positions. Your next move is yours.</div>}</div></section><section className="panel strategy-panel"><div className="panel-title"><div><span className="eyebrow">AUTOMATION</span><h2>Strategy desk</h2></div><button className={`toggle ${strategyEnabled ? "on" : ""}`} onClick={toggleStrategy} aria-label="Toggle strategy"><span /></button></div><p className="strategy-name">Moving average crossover <span>5 / 20</span></p><p className="muted strategy-copy">Signals are evaluated against the live ticker feed. Paper execution only.</p><div className="strategy-stats"><span><b>STATUS</b><strong className={strategyEnabled ? "positive" : "muted"}>{strategyEnabled ? "RUNNING" : "PAUSED"}</strong></span><span><b>MODE</b><strong>SIMULATION</strong></span></div><button className="outline-button" onClick={toggleStrategy}>{strategyEnabled ? "Pause strategy" : "Enable strategy"} <span>→</span></button></section></div>
      <section className="panel orders-panel"><div className="panel-title"><div><span className="eyebrow">ACTIVITY</span><h2>Recent orders</h2></div><span className="muted">Last 8 executions</span></div><div className="orders-table"><div className="table-head"><span>Order</span><span>Side</span><span>Quantity</span><span>Price</span><span>Status</span></div>{orders.map((order) => <div className="asset-row" key={order.id}><span className="asset-name"><b>#{order.id}</b><small>{order.symbol}</small></span><span className={order.side === "BUY" ? "positive" : "negative"}>{order.side}</span><span>{order.quantity}</span><span>{money(order.executedPrice)}</span><span className={order.status === "FILLED" ? "filled" : "rejected"}>{order.status}</span></div>)}{!orders.length && <div className="empty">No orders have been placed yet.</div>}</div></section><footer><span>CRYPTOWATCH / PAPER TERMINAL</span><span>Market data via Binance · Execution simulated locally</span></footer>
    </main>
  );
