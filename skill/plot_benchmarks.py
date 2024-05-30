from matplotlib import pyplot as plt
import os
import json
import math
import numpy as np

# Fit the function y = A * exp(B * x) to the data
# returns (A, B)
# From: https://mathworld.wolfram.com/LeastSquaresFittingExponential.html
def fit_exp(xs, ys):
    S_x2_y = 0.0
    S_y_lny = 0.0
    S_x_y = 0.0
    S_x_y_lny = 0.0
    S_y = 0.0
    for (x,y) in zip(xs, ys):
        S_x2_y += x * x * y
        S_y_lny += y * np.log(y)
        S_x_y += x * y
        S_x_y_lny += x * y * np.log(y)
        S_y += y
    #end
    a = (S_x2_y * S_y_lny - S_x_y * S_x_y_lny) / (S_y * S_x2_y - S_x_y * S_x_y)
    b = (S_y * S_x_y_lny - S_x_y * S_y_lny) / (S_y * S_x2_y - S_x_y * S_x_y)
    return (np.exp(a), b)


benchmark_dirs = sorted(os.listdir("benchmarks/"))
print(benchmark_dirs)
colors = dict(zip(benchmark_dirs, plt.colormaps.get_cmap('viridis').resampled(len(benchmark_dirs)).colors))


input_plots = {}
incremental_bar_plots = {}
incremental_graph_plots = {}

def add_item_to_input_plots(benchmark_file, item_name, benchmark_dir, value):
    key = benchmark_file + " - " + item_name
    if key not in input_plots.keys():
        input_plots[key] = ([], [])
    input_plots[key][0].append(benchmark_dir)
    input_plots[key][1].append(value)

def add_item_to_incremental_bar_plots(benchmark_file, item_name, benchmark_dir, value):
    key = benchmark_file + " - " + item_name
    if key not in incremental_bar_plots.keys():
        incremental_bar_plots[key] = ([], [])
    incremental_bar_plots[key][0].append(benchmark_dir)
    incremental_bar_plots[key][1].append(value)

def add_line_to_incremental_graph_plots(benchmark_file, item_name, benchmark_dir, xs, ys):
    key = benchmark_file + " - " + item_name
    if key not in incremental_graph_plots.keys():
        incremental_graph_plots[key] = []
    incremental_graph_plots[key].append((benchmark_dir, xs, ys))


for benchmark_file in ["current_time", "weather", "timer"]:
    for benchmark_dir in benchmark_dirs:
        data = json.load(open(os.path.join("benchmarks/", benchmark_dir, benchmark_file + ".json")))

        add_item_to_incremental_bar_plots(benchmark_file, "increm", benchmark_dir, len(data["incremental"]))

        for benchmark in data["benchmarks"]:
            add_item_to_input_plots(benchmark_file, str(len(benchmark["input"])) + "ch", benchmark_dir, benchmark["time"] / 1e9)

        add_line_to_incremental_graph_plots(benchmark_file, "increm", benchmark_dir, [v["size"] for v in data["incremental"]], [v["time"] / 1e9 for v in data["incremental"]])


input_plots = {}

plot_count = len(input_plots) + len(incremental_bar_plots) + len(incremental_graph_plots)
plot_height = max(int(math.sqrt(plot_count) + 0.5), 1)
plot_width = (plot_count + plot_height - 1) // plot_height
print(plot_height, plot_width)
assert plot_height * plot_width >= plot_count

for i, (item_name, (benchmark_dirs, values)) in enumerate(input_plots.items()):
    plt.subplot(plot_height, plot_width, i + 1)
    plt.title(item_name, fontsize=9)
    plt.bar(benchmark_dirs, values, color=[colors[benchmark_dir] * 0.7 for benchmark_dir in benchmark_dirs])

for i, (item_name, (benchmark_dirs, values)) in enumerate(incremental_bar_plots.items()):
    plt.subplot(plot_height, plot_width, i + 1 + len(input_plots))
    plt.title(item_name, fontsize=9)
    plt.bar(benchmark_dirs, values, color=[colors[benchmark_dir] for benchmark_dir in benchmark_dirs])

for i, (item_name, data) in enumerate(incremental_graph_plots.items()):
    plt.subplot(plot_height, plot_width, i + 1 + len(input_plots) + len(incremental_bar_plots))
    plt.title(item_name, fontsize=9)
    for (benchmark_dir, xs, ys) in data:
        plt.plot(xs, ys, color=colors[benchmark_dir])
    A, B = fit_exp(xs, ys)
    print(f"{item_name} => O({np.exp(B):.2f}ⁿ)")
    plt.plot(list(range(xs[-1]+1)), [A * np.exp(B * v) for v in range(xs[-1]+1)], color="black", linewidth=1, linestyle='--')

plt.show()
