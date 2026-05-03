Simplified REST API stock exchange simulator written in Java (Spring) with docker + nginx + postgresql deployment

# Running
Unix:

`./run-unix.sh PORT`

Windows *(modern versions should have correctly configured .ps1 association)*

`.\run-win.ps1 -Port PORT`

# Endpoints
| Endpoint                                   | Method | Description                                          |
|--------------------------------------------|--------|------------------------------------------------------|
| `/stocks`                                  | GET    | Returns list of bank stocks                          |
| `/stocks`                                  | POST   | Sets bank stocks                                     |
| `/log`                                     | GET    | Returns log of succesful transactions                |
| `/chaos`                                   | POST   | Kills an instance                                    |
| `/wallets/{wallet_id}/stocks/{stock_name}` | GET    | Quantity of stock in wallet                          |
| `/wallets/{wallet_id}/stocks/{stock_name}` | POST   | Buys/sells stock in wallet (`{"type": "buy\|sell"}`) |
| `/wallets/{wallet_id}`                     | GET    | List of stocks in specified wallet                   |

